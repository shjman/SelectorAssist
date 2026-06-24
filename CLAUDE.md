# SelectorAssist — CLAUDE.md

Read automatically every session. Follow strictly.

---

## Reference docs

Read before looking at source files — they contain ready snippets and patterns.

| File | Read when |
|------|-----------|
| `ARCHITECTURE.md` | new screen, navigation, DI, module structure, MVI/Decompose/Koin patterns |
| `DESIGN_SYSTEM.md` | any UI code: colors, components, new screen, changes to AppColors |

When creating subagents for implementation — pass the relevant file content in the prompt.

---

## Workflow

### Branch: simple vs complex tasks

**Simple tasks** — execute directly, no orchestrator:
- Single-line edits, typos, renames
- Architecture questions, code explanations
- Documentation edits, CLAUDE.md edits, config changes
- Changes that cannot cause a crash or regression

**Complex tasks** — always go through the orchestrator agent (`.claude/agents/orchestrator.md`):
- Any changes to logic, architecture, navigation
- New screens, components, use cases
- DB schema changes, DI module changes
- Refactoring that touches multiple files
- Anything that could cause a crash or regression

When in doubt — choose the orchestrator.

---

### Validation (mandatory after any code changes)

After completion, always run the static analyzers:
```bash
./gradlew detekt --no-configuration-cache
./gradlew lintDebug --no-configuration-cache
scripts/ktlint --relative '**/*.kt' '!**/build/**' --reporter=plain
```
The task is only complete when all three pass without errors. On errors — return to the analysis or execution stage, fix, re-run the check.

If UI files were changed, also verify snapshot tests:
```bash
./gradlew verifyPaparazziDebug --no-configuration-cache   # core:ui components
./gradlew verifyRoborazziDebug --no-configuration-cache   # feature:* screens
```
If snapshots fail due to intentional UI changes — record new baselines and commit them:
```bash
./gradlew recordPaparazziDebug recordRoborazziDebug --no-configuration-cache
```

---

## Work style

**Quality is the only criterion.** Do not accept "we'll fix it later", "good enough for MVP", "temporary solution". Temporary becomes permanent.

**Right to disagree.** If the requested approach violates SOLID, creates tech debt, or is a workaround — object, explain why, propose an alternative. Silent agreement with a bad solution is also a mistake.

**Right to critique and advise.** If you spot a problem in code, architecture, or approach — name it, even if not asked. Propose a better solution. Debate if you see risks.

**Right to ask.** If requirements are unclear, incomplete, or contradictory — ask a question and wait for an answer. Never fill in the blanks for the user.

**Solution priority:** correct > maintainable > fast. Never the reverse.

If the user insists on a bad solution — explicitly name the risks, record disagreement, and only then execute (if the user confirms).

---

## Build commands

```bash
./gradlew :composeApp:assembleDebug          # build debug APK
./gradlew :composeApp:assembleRelease        # build release APK
./gradlew :composeApp:installDebug           # build + install on device/emulator
./gradlew lintDebug --no-configuration-cache # Android Lint
./gradlew detekt --no-configuration-cache    # Kotlin static analysis
```

---

## Developer

Experienced Android developer, deep Kotlin knowledge, standard patterns (MVVM, MVI, Repository, DI) — no explanation needed. Explain KMP-specific things; Android fundamentals — no. Communication: concise, technically precise.

---

## Project

KMP + Compose Multiplatform. Android + iOS, fully shared UI (no SwiftUI).  
Package: `com.yahorshymanchyk.selectorassist`  
Pet project → Google Play + App Store.

**Core idea:** the user creates a binary dilemma (two poles, a deadline). Every day — a slider, tags, comment. At the end — statistics of tendencies + patterns.

---

## Stack (do not change without explicit request)

| Layer | Solution |
|-------|---------|
| UI | Compose Multiplatform (commonMain) |
| Architecture | MVI + plain ViewModel class with external CoroutineScope |
| DB | SQLDelight |
| Navigation | Decompose 3.x (ChildStack, StackNavigation) |
| Async | Coroutines + StateFlow |
| DI | Koin 4.x (KMP) |
| Notifications | Alarmee (local/scheduled, commonMain) |
| Biometry | expect/actual: AndroidX Biometric / LocalAuthentication |
| Build | Gradle KTS + libs.versions.toml |
| JVM | 17 · minSdk 28 · iOS 16.0 |

**material3:** `org.jetbrains.compose.material3:1.10.0-alpha05` — JetBrains CMP artifact, NOT equal to `androidx.compose.material3`. Do not change version without checking compatibility with foundation.

---

## Modules and dependencies

```
:core:domain   — models, repositories (interfaces), use cases
:core:data     — SQLDelight, repository implementations, mappers
:core:ui       — AppTheme, AppColors, AppTypography, shared components
:feature:questions — questions list + create question
:feature:entry     — daily input
:feature:report    — final report
:feature:settings  — settings (biometry)
:composeApp    — entry point, Koin wiring, Decompose Root, BiometryComponent (expect/actual)
```

Rule: `:feature:*` → only `:core:domain` + `:core:ui`, never `:core:data`.  
Exception: `BiometryComponent` lives in `:composeApp` (uses `expect/actual` and owns the gate at RootComponent level).  
All dependency versions — only via `libs.versions.toml`.  
Do not add new dependencies without explicit request.

---

## Architecture

**Layers:** UI → Intent → ViewModel → UseCase → Repository (interface) → SQLDelight

**Component pattern** (detailed + snippets → `ARCHITECTURE.md`):
- Interface: `Value<XxxState>` + `onIntent(XxxIntent)`
- `Default*`: `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, `lifecycle.doOnDestroy { scope.cancel() }`, `MutableValue` bridged from `StateFlow`
- ViewModel: plain class, does not extend `androidx.lifecycle.ViewModel`, scope from outside

**Koin:** `SelectorAssistApp` / `MainViewController` → `androidPlatformModule` (or `iosPlatformModule`) + `domainModule`. Repositories and `CurrentDateProvider` are registered in the platform module as `single`. `DefaultRootComponent : KoinComponent` injects use cases via `by inject()`.

---

## Current implementation status

**Done:**
- `core:domain` — all models (`Question`, `Entry`, `Tag`, `AppSettings`), repositories (interfaces), all use cases
- `core:data` — SQLDelight schema (tables: questions, entries, entry_tags, app_settings), both drivers, repositories, mappers
- `core:ui` — AppTheme, AppColors, AppTypography, BackButton, SettingsIconButton
- `feature:questions` — QuestionsListScreen + CreateQuestionScreen (full MVI + Decompose)
- `feature:entry` — EntryScreen (slider 0..10 + tags + comment, full MVI + Decompose)
- `feature:report` — ReportScreen (tendency + tag influence + arguments, full MVI + Decompose)
- `feature:settings` — SettingsScreen (biometry toggle, full MVI + Decompose)
- `composeApp` — Koin DI, RootComponent (ChildStack: Biometry → Home), HomeComponent with ChildStack, BiometryComponent + expect/actual BiometryAuthenticator, MainActivity

**TODO (MVP):**
- QuestionComponent (nested ChildStack for Entry/Report)
- Alarmee notifications
- DeleteQuestionUseCase — UI (swipe or button); domain + DI already done

---

## Git

```
main ← merge from develop only
develop ← current development
feature/ · fix/ · chore/
```

Commit format: `feat(scope): ...` / `fix(scope): ...` / `chore(scope): ...`

---

<!-- ======================================================= -->
<!-- AGENT SECTIONS — read by label, not the whole file      -->
<!-- ======================================================= -->

## HARD RULES
<!-- read by: ALL agents. Orchestrator copies this section into task.md -->

- No network requests, analytics, sync
- No AI inside the app
- No advice to the user from the app
- No engagement mechanics (streaks, rewards, pressure reminders)
- `Color(0xFF...)` inline — forbidden, only via `AppColors`
- `LiveData` / `MutableLiveData` — forbidden
- `class XxxViewModel : ViewModel()` — forbidden, only plain class with external scope
- Business logic in `@Composable` or Component — forbidden
- `:feature:*` → `implementation(projects.core.data)` — forbidden
- `import android.*` in `:core:domain` or `:core:data` — forbidden

---

## ARCHITECTURE
<!-- read by: Planner -->

**Stack:**
- UI: Compose Multiplatform, commonMain only — no SwiftUI
- Architecture: MVI + plain ViewModel with external CoroutineScope
- Navigation: Decompose 3.x (ChildStack, StackNavigation)
- DI: Koin 4.x (KMP)
- DB: SQLDelight
- Async: Coroutines + StateFlow

**Layers:** UI → Intent → ViewModel → UseCase → Repository (interface) → SQLDelight

**Modules:**
```
:core:domain   — models, repositories (interfaces), use cases
:core:data     — SQLDelight, repository implementations, mappers
:core:ui       — AppTheme, AppColors, AppTypography, shared components
:feature:*     — only :core:domain + :core:ui, never :core:data
:composeApp    — entry point, Koin wiring, Decompose Root
```

**Dependency rules:**
- All versions — only via `libs.versions.toml`
- Do not add new dependencies without explicit request
- `BiometryComponent` lives in `:composeApp` (exception — uses expect/actual)

**Component pattern:**
- Interface: `Value<XxxState>` + `onIntent(XxxIntent)`
- `Default*`: `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, `lifecycle.doOnDestroy { scope.cancel() }`
- ViewModel: plain class, scope from outside, does not extend `androidx.lifecycle.ViewModel`

**Koin:** repositories and `CurrentDateProvider` — `single` in platform module. `DefaultRootComponent : KoinComponent` injects use cases via `by inject()`.

---

## CODE STYLE
<!-- read by: Executor -->

- `BasicTextField` instead of M3 `TextField`
- Magic numbers → named `private const val`
- `@Suppress` only with a one-line justification comment
- `import android.*` in `:core:domain` or `:core:data` — forbidden
- Dependency versions only via `libs.versions.toml`
- Material3: `org.jetbrains.compose.material3:1.10.0-alpha05` — JetBrains CMP artifact, do not change without checking compatibility

---

## REVIEW CHECKLIST
<!-- read by: Reviewer -->

**Plan:**
- All steps from plan.md completed
- No unplanned changes

**Code:**
- All HARD RULES followed (see section above)
- No magic numbers without `private const val`
- `@Suppress` — only with a comment

**Static analyzers (all three required):**
```bash
./gradlew detekt --no-configuration-cache
./gradlew lintDebug --no-configuration-cache
scripts/ktlint --relative '**/*.kt' '!**/build/**' --reporter=plain
```

**Snapshot tests (if UI files changed):**
```bash
./gradlew verifyPaparazziDebug --no-configuration-cache
./gradlew verifyRoborazziDebug --no-configuration-cache
```
