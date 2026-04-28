# SelectorAssist — CLAUDE.md

Читается автоматически каждую сессию. Следуй строго.

---

## Reference docs

Читай до того, как смотреть исходники — содержат готовые сниппеты и паттерны.

| Файл | Читать когда |
|------|-------------|
| `ARCHITECTURE.md` | новый экран, навигация, DI, структура модулей, паттерны MVI/Decompose/Koin |
| `DESIGN_SYSTEM.md` | любой UI-код: цвета, компоненты, новый экран, изменения в AppColors |

При создании субагентов на реализацию — передавай содержимое релевантного файла в промпт.

---

## Стиль работы

**Качество важнее скорости.** Не принимать «починим потом», «сойдёт для MVP», «временное решение». Временное становится постоянным.

**Право отказать.** Если запрошенный подход нарушает SOLID, создаёт tech debt или является workaround — возразить, объяснить почему, предложить альтернативу. Молчаливое согласие с плохим решением — тоже ошибка. Если пользователь настаивает — явно назвать риски перед тем как продолжить.

**Быстрый путь:** однострочные правки, опечатки, очевидные переименования — делать сразу без обсуждения.

**Нетривиальные задачи:** уточнять до полной ясности требований, затем реализовывать пошагово.

---

## Build commands

```bash
./gradlew :composeApp:assembleDebug          # сборка debug APK
./gradlew :composeApp:assembleRelease        # сборка release APK
./gradlew :composeApp:installDebug           # сборка + установка на устройство/эмулятор
./gradlew lintDebug --no-configuration-cache # Android Lint
./gradlew detekt --no-configuration-cache    # Kotlin статический анализ
```

Оба — `lintDebug` и `detekt` — должны быть чистыми до коммита.

---

## Разработчик

Опытный Android-разработчик, знает Kotlin глубоко, стандартные паттерны (MVVM, MVI, Repository, DI) — без объяснений. KMP-специфику объясняй, Android-базу — нет. Общение лаконичное, технически точное.

---

## Проект

KMP + Compose Multiplatform. Android + iOS, UI полностью общий (никакого SwiftUI).  
Package: `com.yahorshymanchyk.selectorassist`  
Pet project → Google Play + App Store.

**Суть:** пользователь создаёт бинарную дилемму (два полюса, срок). Каждый день — слайдер, теги, комментарий. По окончании — статистика склонений + паттерны.

---

## Стек (не менять без явного запроса)

| Слой | Решение |
|------|---------|
| UI | Compose Multiplatform (commonMain) |
| Архитектура | MVI + plain ViewModel class с внешним CoroutineScope |
| БД | SQLDelight |
| Навигация | Decompose 3.x (ChildStack, StackNavigation) |
| Async | Coroutines + StateFlow |
| DI | Koin 4.x (KMP) |
| Уведомления | Alarmee (local/scheduled, commonMain) |
| Биометрия | expect/actual: AndroidX Biometric / LocalAuthentication |
| Сборка | Gradle KTS + libs.versions.toml |
| JVM | 17 · minSdk 28 · iOS 16.0 |

**material3:** `org.jetbrains.compose.material3:1.10.0-alpha05` — JetBrains CMP-артефакт, НЕ равен `androidx.compose.material3`. Не менять версию без проверки совместимости с foundation.

---

## Модули и зависимости

```
:core:domain   — модели, репозитории (интерфейсы), use cases
:core:data     — SQLDelight, реализации репозиториев, маперы
:core:ui       — AppTheme, AppColors, AppTypography, shared components
:feature:questions — список + создание вопроса
:feature:entry     — ежедневный ввод
:feature:report    — финальный отчёт
:composeApp    — точка входа, Koin wiring, Decompose Root
```

Правило: `:feature:*` → только `:core:domain` + `:core:ui`, никогда `:core:data`.  
Все версии зависимостей — только через `libs.versions.toml`.  
Не добавлять новые зависимости без явного запроса.

---

## Архитектура

**Слои:** UI → Intent → ViewModel → UseCase → Repository (interface) → SQLDelight

**Паттерн компонента** (подробно + сниппеты → `ARCHITECTURE.md`):
- Интерфейс: `Value<XxxState>` + `onIntent(XxxIntent)`
- `Default*`: `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, `lifecycle.doOnDestroy { scope.cancel() }`, `MutableValue` bridged из `StateFlow`
- ViewModel: plain class, не наследует `androidx.lifecycle.ViewModel`, scope снаружи

**Koin:** `SelectorAssistApp` / `MainViewController` → `androidPlatformModule` + `dataModule` + `domainModule`. `DefaultRootComponent : KoinComponent`.

---

## Текущий статус реализации

**Готово:**
- `core:domain` — все модели, репозитории, все use cases
- `core:data` — SQLDelight схема, оба драйвера, репозитории, маперы
- `core:ui` — AppTheme, AppColors, AppTypography
- `feature:questions` — QuestionsListScreen + CreateQuestionScreen (полный MVI + Decompose)
- `feature:entry` — EntryScreen (слайдер + теги + комментарий, полный MVI + Decompose)
- `composeApp` — Koin DI, RootComponent, HomeComponent с ChildStack, MainActivity

**TODO (MVP):**
- `feature:report` — ReportComponent, ReportScreen
- QuestionComponent (вложенный ChildStack для Entry/Report)
- BiometryComponent
- Alarmee уведомления
- DeleteQuestionUseCase — UI (свайп или кнопка)

---

## Никогда не использовать

- `class XxxViewModel : ViewModel()` — только plain class с внешним scope
- `import android.*` в `:core:domain` или `:core:data` (кроме SQLDelight-драйвера на Android)
- `:feature:*` → `implementation(projects.core.data)` — запрещено
- `LiveData` / `MutableLiveData`
- Бизнес-логику в `@Composable` или Component — только в ViewModel/UseCase
- `Color(0xFF...)` inline в экранах — только через `AppColors`
- Magic numbers без именованных `private const val`
- `@Suppress` без однострочного комментария-обоснования

---

## Абсолютные запреты (продукт)

- Никаких сетевых запросов (ни аналитики, ни синхронизации)
- Никакого ИИ внутри приложения
- Никаких советов пользователю от приложения
- Никаких engagement-механик (стрики, награды, напоминания-давления)

---

## Git

```
main ← только merge из develop
develop ← текущая разработка
feature/ · fix/ · chore/
```

Формат коммита: `feat(scope): ...` / `fix(scope): ...` / `chore(scope): ...`
