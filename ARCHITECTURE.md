# SelectorAssist — Architecture Guide

KMP + Compose Multiplatform. Android + iOS, shared UI in `commonMain`.  
Stack: MVI · Decompose · SQLDelight · Koin · Coroutines/StateFlow

---

## Module structure

```
:core:domain   — models, repository interfaces, use cases  (pure Kotlin, zero Android)
:core:data     — SQLDelight, repository implementations, mappers
:core:ui       — AppTheme, AppColors, AppTypography, shared Compose components
:feature:questions — QuestionsListScreen + CreateQuestionScreen
:feature:entry     — EntryScreen (daily slider + tags + comment)
:feature:report    — ReportScreen (tendency + tag influence + arguments)
:feature:settings  — SettingsScreen (biometry toggle)
:composeApp    — entry point, Koin wiring, Decompose Root, BiometryComponent, MainActivity / MainViewController
```

### Dependency rules

```
:feature:*   →  :core:domain  +  :core:ui        ✅
:core:data   →  :core:domain                      ✅
:composeApp  →  all modules                       ✅

:feature:*   →  :core:data                        ❌ FORBIDDEN
:core:domain →  anything Android / platform       ❌ FORBIDDEN
```

> **Exception:** `BiometryComponent` lives in `:composeApp` (not a feature module) because it uses
> `expect/actual` (`BiometryAuthenticator`) and owns the app-level `RootComponent` navigation gate.

---

## Layer flow

```
UI (Screen.kt)
  └─ onIntent() ──► Component
                        └─ viewModel.onIntent() ──► ViewModel
                                                        └─ UseCase
                                                              └─ Repository (interface)
                                                                    └─ RepositoryImpl (SQLDelight)
```

---

## MVI pattern

### State
```kotlin
// Immutable snapshot. All fields have defaults → safe MutableStateFlow(XxxState()).
data class XxxState(
    val title: String = "",
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean get() = title.isNotBlank() && !isLoading  // derived, not stored
}
```

### Intent
```kotlin
sealed interface XxxIntent {
    data class UpdateTitle(val value: String) : XxxIntent
    data object Submit : XxxIntent
}
```

### ViewModel
```kotlin
/**
 * ARCH: Plain-class ViewModel (not androidx.lifecycle.ViewModel)
 * WHY: commonMain has no JVM/Android runtime. Lifecycle is owned by Decompose ComponentContext.
 */
class XxxViewModel(
    private val scope: CoroutineScope,       // injected from Component, cancelled on destroy
    private val someUseCase: SomeUseCase,
    private val onDone: () -> Unit,
) {
    private val _state = MutableStateFlow(XxxState())
    val state: StateFlow<XxxState> = _state.asStateFlow()

    init {
        scope.launch {
            someUseCase().collect { data ->
                _state.update { it.copy(title = data.title) }
            }
        }
    }

    fun onIntent(intent: XxxIntent) {
        when (intent) {
            is XxxIntent.UpdateTitle -> _state.update { it.copy(title = intent.value) }
            XxxIntent.Submit         -> submit()
        }
    }

    private fun submit() {
        if (_state.value.isLoading) return
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            someUseCase.create(_state.value.title)
            onDone()
        }
    }
}
```

---

## Decompose Component pattern

### Interface
```kotlin
// Expose only what the UI needs. Never expose ViewModel directly.
interface XxxComponent {
    val state: Value<XxxState>       // Decompose Value, not StateFlow
    fun onIntent(intent: XxxIntent)
    fun onBack()
}
```

### Default implementation
```kotlin
/**
 * ARCH: DefaultXxxComponent bridges Decompose lifecycle ↔ Kotlin coroutines
 * WHY: Decompose uses its own Value<T>, not StateFlow. MutableValue acts as the bridge.
 *      CoroutineScope is cancelled via doOnDestroy — no leaks on back-stack pop.
 */
class DefaultXxxComponent(
    componentContext: ComponentContext,
    private val onNavigateBack: () -> Unit,
    someUseCase: SomeUseCase,
) : XxxComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel = XxxViewModel(
        scope = scope,
        someUseCase = someUseCase,
        onDone = onNavigateBack,
    )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<XxxState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    override fun onIntent(intent: XxxIntent) = viewModel.onIntent(intent)
    override fun onBack() = onNavigateBack()
}
```

---

## Navigation (Decompose ChildStack)

```kotlin
/**
 * ARCH: HomeConfig is a sealed interface used as ChildStack configuration
 * WHY: serializer = null because no process-death restore needed (pet project, simple nav).
 *      Each config maps to exactly one HomeChild via createChild().
 */
private sealed interface HomeConfig {
    data object QuestionsList  : HomeConfig
    data object CreateQuestion : HomeConfig
    data class  Entry(val questionId: Long)  : HomeConfig
    data class  Report(val questionId: Long) : HomeConfig
}
```

### Current Decompose tree

```
RootComponent (ChildStack)
├── BiometryComponent            ✅  (composeApp, expect/actual BiometryAuthenticator)
└── HomeComponent (ChildStack)
    ├── QuestionsListComponent   ✅
    ├── CreateQuestionComponent  ✅
    ├── EntryComponent           ✅
    ├── ReportComponent          ✅
    └── SettingsComponent        ✅

Planned (MVP):
RootComponent (ChildStack)
├── BiometryComponent            ✅
└── HomeComponent (ChildStack)
    ├── QuestionsListComponent   ✅
    ├── CreateQuestionComponent  ✅
    ├── SettingsComponent        ✅
    └── QuestionComponent (nested ChildStack) TODO
        ├── EntryComponent       ✅  (to move here)
        └── ReportComponent      ✅  (to move here)
```

### Adding a destination to HomeComponent

1. Add `data class/object XxxConfig` to `HomeConfig` sealed interface in `DefaultHomeComponent`
2. Add `class Xxx(val component: XxxComponent) : HomeChild()` to `HomeComponent.HomeChild`
3. Add `is HomeConfig.Xxx -> HomeComponent.HomeChild.Xxx(DefaultXxxComponent(...))` in `createChild()`
4. Add the trigger: `navigation.push(HomeConfig.Xxx(...))` where needed
5. Add render in `HomeContent` in `RootContent.kt`

---

## DI (Koin)

```
Platform module (androidPlatformModule / iosPlatformModule)
    └─ single<CurrentDateProvider>  { SystemCurrentDateProvider() }
    └─ single  { DatabaseDriverFactory(...) }
    └─ single  { AppDatabase(driverFactory.create()) }
    └─ single<QuestionRepository>    { QuestionRepositoryImpl(db) }
    └─ single<EntryRepository>       { EntryRepositoryImpl(db) }
    └─ single<AppSettingsRepository> { AppSettingsRepositoryImpl(db) }

domainModule  (composeApp/di/AppModule.kt)
    └─ factory { GetXxxUseCase(get()) }   // new use case = one line here

DefaultRootComponent : KoinComponent
    └─ inject() use cases
    └─ passes to DefaultHomeComponent and DefaultBiometryComponent constructors
```

**Rule:** Use cases are `factory` (new instance per injection). Repositories and infrastructure are `single`.

### Adding a new use case to DI

1. Write `GetXxxUseCase` in `:core:domain`
2. Add `factory { GetXxxUseCase(get()) }` in `domainModule` (`composeApp/di/AppModule.kt`)
3. Inject in `DefaultRootComponent` via `by inject()`, pass to the relevant component constructor

---

## Recipe: Adding a new feature screen

```
feature/xxx/src/commonMain/kotlin/.../xxx/
├── presentation/
│   ├── XxxState.kt       — data class, all defaults
│   ├── XxxIntent.kt      — sealed interface
│   └── XxxViewModel.kt   — plain class, scope from outside
├── component/
│   ├── XxxComponent.kt          — interface: Value<XxxState> + onIntent + onBack
│   └── DefaultXxxComponent.kt  — ComponentContext by componentContext pattern
└── ui/
    └── XxxScreen.kt      — @Composable fun XxxScreen(component: XxxComponent)
```

**Checklist:**
- [ ] `XxxState` / `XxxIntent` / `XxxViewModel`
- [ ] `XxxComponent` interface + `DefaultXxxComponent`
- [ ] `XxxScreen` composable
- [ ] `HomeChild.Xxx` in `HomeComponent.kt`
- [ ] `HomeConfig.Xxx` + `createChild` case in `DefaultHomeComponent.kt`
- [ ] Use cases added to `DefaultHomeComponent` constructor
- [ ] Injected in `DefaultRootComponent`
- [ ] Render case in `RootContent.kt`
- [ ] `./gradlew :feature:xxx:detekt lintDebug --no-configuration-cache` clean

---

## SOLID in this project

| Principle | Applied as |
|-----------|-----------|
| **S** — Single Responsibility | Screen = UI only · ViewModel = business logic only · Component = lifecycle + navigation only |
| **O** — Open/Closed | `sealed interface Intent` — add new action without modifying existing `when` branches in other places |
| **L** — Liskov | Component interface allows test/preview substitutions without breaking callers |
| **I** — Interface Segregation | Component exposes only `state` + `onIntent` + `onBack`; ViewModel internals stay hidden |
| **D** — Dependency Inversion | ViewModel depends on `UseCase`, not `RepositoryImpl`; `UseCase` depends on `Repository` interface |

---

## Anti-patterns

```kotlin
// ❌ Android ViewModel in commonMain
class XxxViewModel : ViewModel() { ... }
// ✅ Plain class with injected scope

// ❌ Repository directly in ViewModel
class XxxViewModel(private val repo: QuestionRepositoryImpl) { ... }
// ✅ Use case as the boundary
class XxxViewModel(private val getQuestions: GetActiveQuestionsUseCase) { ... }

// ❌ Business logic in Component
class DefaultXxxComponent(...) {
    fun onItemClick(id: Long) {
        if (someCondition) navigation.push(...) else { /* logic */ }  // ❌
    }
}
// ✅ Component only dispatches intents and handles navigation callbacks

// ❌ Android import in :core:domain
import android.content.Context  // ❌ breaks iOS build

// ❌ :feature accessing :core:data
implementation(projects.core.data)  // in feature/xxx/build.gradle.kts ❌

// ❌ Hardcoded hex in Screen
Text(color = Color(0xFF0A84FF))  // ❌
// ✅
Text(color = AppColors.PoleA)
```

---

## Absolute constraints (product)

- No network requests (no analytics, no sync)
- No AI inside the app
- No advice or suggestions to the user
- No engagement mechanics (streaks, rewards, pressure reminders)
