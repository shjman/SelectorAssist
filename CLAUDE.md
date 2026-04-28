# SelectorAssist — CLAUDE.md

Читается автоматически каждую сессию. Следуй строго.

---

## Разработчик

Опытный Android-разработчик, знает Kotlin глубоко, стандартные паттерны (MVVM, MVI, Repository, DI) — без объяснений. KMP-специфику объясняй, Android-базу — нет. Общение лаконичное, технически точное.

---

## Проект

KMP + Compose Multiplatform. Android + iOS, UI полностью общий (никакого SwiftUI).
Package: `com.yahorshymanchyk.selectorassist`
Pet project → Google Play + App Store.

**Суть:** пользователь создаёт бинарную дилемму (два полюса, срок). Каждый день — слайдер (1–5), теги, комментарий. По окончании — статистика склонений + паттерны.

---

## Абсолютные запреты

- Никаких сетевых запросов (ни аналитики, ни синхронизации)
- Никакого ИИ внутри приложения
- Никаких советов пользователю от приложения
- Никаких engagement-механик (стрики, награды, напоминания-давления)

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

**material3:** `org.jetbrains.compose.material3:1.10.0-alpha05` — это JetBrains CMP-артефакт, НЕ равен `androidx.compose.material3`. Не менять версию без проверки совместимости с foundation.

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

Правило зависимостей: `:feature:*` → только `:core:domain` + `:core:ui`, никогда `:core:data`.

---

## Архитектура

**Слои:** UI → Intent → ViewModel → UseCase → Repository (interface) → SQLDelight

**Decompose-иерархия (целевая):**
```
RootComponent
├── BiometryComponent          — TODO
└── HomeComponent (ChildStack)
    ├── QuestionsListComponent — ✅ реализован
    ├── CreateQuestionComponent — ✅ реализован
    └── QuestionComponent (ChildStack) — TODO
        ├── EntryComponent
        └── ReportComponent
```

**Паттерн компонента:**
- Интерфейс: `Value<XxxState>` + `onIntent(XxxIntent)`
- Реализация `Default*`: `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, `lifecycle.doOnDestroy { scope.cancel() }`, `MutableValue` bridged из `StateFlow`
- ViewModel: plain class, не наследует `androidx.lifecycle.ViewModel`, получает scope снаружи

**MVI:** `XxxState.kt` / `XxxIntent.kt` / `XxxViewModel.kt` в `presentation/`

**Koin:** `SelectorAssistApp` (Android) / `MainViewController` (iOS) → `androidPlatformModule` + `dataModule` + `domainModule`. `DefaultRootComponent : KoinComponent`.

---

## Текущий статус реализации

**Готово:**
- `core:domain` — все модели (Question, Entry, Tag, QuestionStats, ActiveQuestionSummary, CompletedQuestionSummary), репозитории, все use cases
- `core:data` — SQLDelight схема (questions/entries/entry_tags), оба драйвера, репозитории, маперы
- `core:ui` — AppTheme, AppColors, AppTypography
- `feature:questions` — QuestionsListScreen + CreateQuestionScreen (полный MVI + Decompose)
- `composeApp` — Koin DI, RootComponent, HomeComponent с ChildStack, MainActivity, SelectorAssistApp

**TODO (MVP):**
- `feature:entry` — EntryComponent, EntryScreen (слайдер + теги + комментарий)
- `feature:report` — ReportComponent, ReportScreen
- QuestionComponent (вложенный ChildStack для Entry/Report)
- BiometryComponent
- Alarmee уведомления
- DeleteQuestionUseCase — UI (свайп или кнопка в деталях)

---

## Правила кода

- Идиоматичный Kotlin, без лишних абстракций
- Комментарии на английском, только если WHY неочевидно
- 1 файл = 1 класс (исключение: мелкие data class рядом с владельцем)
- Интерфейс — только если несколько реализаций, тесты или expect/actual
- Все версии — только через `libs.versions.toml`
- Не добавлять зависимости без явного запроса
- Magic numbers → именованные константы; hex-цвета в `AppColors` → `@file:Suppress("MagicNumber")` с пометкой

---

## Рабочий процесс

1. **Анализ** — затронутые файлы, зависимости
2. **План** — шаги; согласовать при неоднозначности
3. **Реализация**
4. **Проверка:** `./gradlew lintDebug detekt` — оба чистые до коммита

`@Suppress` — только с явным обоснованием в комментарии.

---

## Статический анализ

```bash
./gradlew lintDebug detekt --no-configuration-cache
---

## Git

```
main ← только merge из develop
develop ← текущая разработка
feature/ · fix/ · chore/
```

Формат коммита: `feat(scope): ...` / `fix(scope): ...` / `chore(scope): ...`
