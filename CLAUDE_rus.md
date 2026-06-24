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

## Рабочий процесс

### Развилка: простые vs сложные задачи

**Простые задачи** — выполнять напрямую, без оркестратора:
- Однострочные правки, опечатки, переименования
- Вопросы об архитектуре, объяснения кода
- Правки в документации, CLAUDE.md, конфигах
- Изменения, которые не могут привести к крашу или регрессии

**Сложные задачи** — обязательно через агента-оркестратора (`.claude/agents/orchestrator.md`):
- Любые изменения логики, архитектуры, навигации
- Новые экраны, компоненты, use cases
- Изменения схемы БД, DI-модулей
- Рефакторинг, затрагивающий несколько файлов
- Всё, что может вызвать краш или регрессию

Сомневаешься — выбирай оркестратора.

---

### Валидация (обязательно после любых изменений кода)

После завершения обязательно прогнать статические анализаторы:
```bash
./gradlew detekt --no-configuration-cache
./gradlew lintDebug --no-configuration-cache
scripts/ktlint --relative '**/*.kt' '!**/build/**' --reporter=plain
```
Задача считается выполненной только если все три прошли без ошибок. При ошибках — вернуться на этап анализа или выполнения, исправить, повторить проверку.

Если изменялись UI-файлы — дополнительно проверить snapshot-тесты:
```bash
./gradlew verifyPaparazziDebug --no-configuration-cache   # компоненты core:ui
./gradlew verifyRoborazziDebug --no-configuration-cache   # экраны feature:*
```
Если snapshot-тесты падают из-за намеренного изменения UI — записать новые baseline и закоммитить их:
```bash
./gradlew recordPaparazziDebug recordRoborazziDebug --no-configuration-cache
```

---

## Стиль работы

**Качество — единственный критерий.** Не принимать «починим потом», «сойдёт для MVP», «временное решение». Временное становится постоянным.

**Право не соглашаться.** Если запрошенный подход нарушает SOLID, создаёт tech debt или является workaround — возразить, объяснить почему, предложить альтернативу. Молчаливое согласие с плохим решением — тоже ошибка.

**Право критиковать и советовать.** Замечаешь проблему в коде, архитектуре или подходе — называй её, даже если тебя об этом не спрашивали. Предлагай лучшее решение. Дискутируй, если видишь риски.

**Право спрашивать.** Если требования неясны, неполны или противоречивы — задать вопрос и дождаться ответа. Никогда не додумывать за пользователя.

**Приоритет решений:** правильное > поддерживаемое > быстрое. Никогда наоборот.

Если пользователь настаивает на плохом решении — явно назвать риски, зафиксировать несогласие, и только потом выполнить (если пользователь подтверждает).

---

## Build commands

```bash
./gradlew :composeApp:assembleDebug          # сборка debug APK
./gradlew :composeApp:assembleRelease        # сборка release APK
./gradlew :composeApp:installDebug           # сборка + установка на устройство/эмулятор
./gradlew lintDebug --no-configuration-cache # Android Lint
./gradlew detekt --no-configuration-cache    # Kotlin статический анализ
```

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
:feature:settings  — настройки (биометрия)
:composeApp    — точка входа, Koin wiring, Decompose Root, BiometryComponent (expect/actual)
```

Правило: `:feature:*` → только `:core:domain` + `:core:ui`, никогда `:core:data`.  
Исключение: `BiometryComponent` живёт в `:composeApp` (использует `expect/actual` и владеет gate-ом на уровне RootComponent).  
Все версии зависимостей — только через `libs.versions.toml`.  
Не добавлять новые зависимости без явного запроса.

---

## Архитектура

**Слои:** UI → Intent → ViewModel → UseCase → Repository (interface) → SQLDelight

**Паттерн компонента** (подробно + сниппеты → `ARCHITECTURE.md`):
- Интерфейс: `Value<XxxState>` + `onIntent(XxxIntent)`
- `Default*`: `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, `lifecycle.doOnDestroy { scope.cancel() }`, `MutableValue` bridged из `StateFlow`
- ViewModel: plain class, не наследует `androidx.lifecycle.ViewModel`, scope снаружи

**Koin:** `SelectorAssistApp` / `MainViewController` → `androidPlatformModule` (или `iosPlatformModule`) + `domainModule`. Репозитории и `CurrentDateProvider` регистрируются в platform-модуле как `single`. `DefaultRootComponent : KoinComponent` инжектирует use cases через `by inject()`.

---

## Текущий статус реализации

**Готово:**
- `core:domain` — все модели (`Question`, `Entry`, `Tag`, `AppSettings`), репозитории (интерфейсы), все use cases
- `core:data` — SQLDelight схема (tables: questions, entries, entry_tags, app_settings), оба драйвера, репозитории, маперы
- `core:ui` — AppTheme, AppColors, AppTypography, BackButton, SettingsIconButton
- `feature:questions` — QuestionsListScreen + CreateQuestionScreen (полный MVI + Decompose)
- `feature:entry` — EntryScreen (слайдер 0..10 + теги + комментарий, полный MVI + Decompose)
- `feature:report` — ReportScreen (склонение + влияние тегов + аргументы, полный MVI + Decompose)
- `feature:settings` — SettingsScreen (toggle биометрии, полный MVI + Decompose)
- `composeApp` — Koin DI, RootComponent (ChildStack: Biometry → Home), HomeComponent с ChildStack, BiometryComponent + expect/actual BiometryAuthenticator, MainActivity

**TODO (MVP):**
- QuestionComponent (вложенный ChildStack для Entry/Report)
- Alarmee уведомления
- DeleteQuestionUseCase — UI (свайп или кнопка); domain + DI уже готовы

---

## Git

```
main ← только merge из develop
develop ← текущая разработка
feature/ · fix/ · chore/
```

Формат коммита: `feat(scope): ...` / `fix(scope): ...` / `chore(scope): ...`

---

<!-- ======================================================= -->
<!-- СЕКЦИИ ДЛЯ АГЕНТОВ — читать по метке, не целиком файл  -->
<!-- ======================================================= -->

## HARD RULES
<!-- читают: ВСЕ агенты. Оркестратор копирует эту секцию в task.md -->

- Никаких сетевых запросов, аналитики, синхронизации
- Никакого ИИ внутри приложения
- Никаких советов пользователю от приложения
- Никаких engagement-механик (стрики, награды, напоминания-давления)
- `Color(0xFF...)` inline — запрещено, только через `AppColors`
- `LiveData` / `MutableLiveData` — запрещено
- `class XxxViewModel : ViewModel()` — запрещено, только plain class с внешним scope
- Бизнес-логику в `@Composable` или Component — запрещено
- `:feature:*` → `implementation(projects.core.data)` — запрещено
- `import android.*` в `:core:domain` или `:core:data` — запрещено

---

## ARCHITECTURE
<!-- читает: Planner -->

**Стек:**
- UI: Compose Multiplatform, commonMain only — никакого SwiftUI
- Архитектура: MVI + plain ViewModel с внешним CoroutineScope
- Навигация: Decompose 3.x (ChildStack, StackNavigation)
- DI: Koin 4.x (KMP)
- БД: SQLDelight
- Async: Coroutines + StateFlow

**Слои:** UI → Intent → ViewModel → UseCase → Repository (interface) → SQLDelight

**Модули:**
```
:core:domain   — модели, репозитории (интерфейсы), use cases
:core:data     — SQLDelight, реализации репозиториев, маперы
:core:ui       — AppTheme, AppColors, AppTypography, shared components
:feature:*     — только :core:domain + :core:ui, никогда :core:data
:composeApp    — точка входа, Koin wiring, Decompose Root
```

**Правила зависимостей:**
- Все версии — только через `libs.versions.toml`
- Не добавлять новые зависимости без явного запроса
- `BiometryComponent` живёт в `:composeApp` (исключение — использует expect/actual)

**Паттерн компонента:**
- Интерфейс: `Value<XxxState>` + `onIntent(XxxIntent)`
- `Default*`: `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, `lifecycle.doOnDestroy { scope.cancel() }`
- ViewModel: plain class, scope снаружи, не наследует `androidx.lifecycle.ViewModel`

**Koin:** репозитории и `CurrentDateProvider` — `single` в platform-модуле. `DefaultRootComponent : KoinComponent` инжектирует use cases через `by inject()`.

---

## CODE STYLE
<!-- читает: Executor -->

- `BasicTextField` вместо M3 `TextField`
- Magic numbers → именованные `private const val`
- `@Suppress` только с однострочным комментарием-обоснованием
- `import android.*` в `:core:domain` или `:core:data` — запрещено
- Версии зависимостей только через `libs.versions.toml`
- Material3: `org.jetbrains.compose.material3:1.10.0-alpha05` — JetBrains CMP-артефакт, не менять без проверки совместимости

---

## REVIEW CHECKLIST
<!-- читает: Reviewer -->

**План:**
- Все шаги из plan.md выполнены
- Нет незапланированных изменений

**Код:**
- Все HARD RULES соблюдены (см. секцию выше)
- Нет magic numbers без `private const val`
- `@Suppress` — только с комментарием

**Статические анализаторы (все три обязательны):**
```bash
./gradlew detekt --no-configuration-cache
./gradlew lintDebug --no-configuration-cache
scripts/ktlint --relative '**/*.kt' '!**/build/**' --reporter=plain
```

**Snapshot-тесты (если изменялись UI-файлы):**
```bash
./gradlew verifyPaparazziDebug --no-configuration-cache
./gradlew verifyRoborazziDebug --no-configuration-cache
```
