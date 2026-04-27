# CLAUDE.md — SelectorAssist

Этот файл читается Claude Code автоматически при каждой сессии.
Следуй инструкциям строго. Не отступай от стека и принципов без явного запроса разработчика.

---

## Что за проект

KMP (Kotlin Multiplatform) приложение с Compose Multiplatform UI.
Целевые платформы: Android и iOS. UI полностью общий — никакого SwiftUI.
Pet project одного разработчика. Цель — рабочее приложение в Google Play и App Store.

Кодовое название: **SelectorAssist**
Финальное название: **Ambivalence Monitor** (уточняется)
Package: `com.yahorshymanchyk.selectorassist`

---

## Суть приложения

Пользователь создаёт бинарную дилемму с двумя полюсами и сроком наблюдения.
Каждый день: двигает ползунок (5 позиций), опционально выбирает теги и пишет комментарий.
По окончании срока: статистика склонений + паттерн источников + аргументы по полюсам.

---

## Стиль сотрудничества

- Claude **может и должен не соглашаться** с идеями разработчика — аргументированно, без агрессии
- Claude **предлагает альтернативы**, если видит более подходящее решение
- **Уточняющие вопросы обязательны** перед реализацией неоднозначной задачи
- **Не додумывать** — лучше спросить, чем реализовать не то
- Если решение кажется избыточным, преждевременным или противоречит стеку — сказать об этом явно

---

## Абсолютные запреты (Anti-AI Manifesto)

- **Никаких сетевых запросов.** Ни аналитики, ни телеметрии, ни синхронизации.
- **Никакого ИИ внутри приложения.** Никаких ML-моделей, никаких API.
- **Никаких советов пользователю** от приложения — только его собственные данные.
- **Никаких engagement-механик** — стриков, наград, напоминаний "ты не заходил N дней".

Нарушение любого из этих пунктов недопустимо даже если кажется полезным.

---

## Технический стек (не менять без явного запроса)

| Слой | Решение |
|------|---------|
| UI | Compose Multiplatform (commonMain) |
| Архитектура | MVI + shared ViewModel |
| ViewModel | `androidx.lifecycle:lifecycle-viewmodel` (KMP) |
| БД | SQLDelight |
| Навигация | Decompose |
| Async | Kotlin Coroutines + StateFlow |
| DI | Koin (KMP-совместимый) |
| Уведомления | Alarmee (локальные scheduled, commonMain) |
| Биометрия | expect/actual вручную (AndroidX Biometric на Android, LocalAuthentication на iOS) |
| Сборка | Gradle KTS + Version Catalogs (libs.versions.toml) |
| JVM Target | 17 |
| Min Android SDK | 28 |
| iOS deployment | 16.0 |

### Примечания по стеку

**Alarmee** — библиотека для локальных и scheduled уведомлений в KMP.
Используем только local/scheduled notifications, никакого Firebase/push.
Документация: https://github.com/tweener/alarmee

**Биометрия через expect/actual** — не библиотека.
Android: `androidx.biometric:biometric` в androidMain.
iOS: `LocalAuthentication` framework, вызывается из iosMain через Kotlin/Native.
Интерфейс определяется в commonMain как `expect class BiometryManager`.

---

## Структура модулей

Проект многомодульный. Каждый модуль — отдельный Gradle-модуль с KMP-конфигурацией.

```
:core:domain        — доменные модели, интерфейсы репозиториев, все UseCase'ы
:core:data          — SQLDelight схема и драйверы, реализации репозиториев, маперы
:core:ui            — общие Compose-компоненты, тема, цвета, типографика
:feature:questions  — список вопросов + экран создания вопроса
:feature:entry      — экран ежедневного ввода (слайдер, теги, комментарий)
:feature:report     — экран финального отчёта
:app (composeApp)   — точка входа Android/iOS, Koin wiring, Decompose RootComponent
```

### Зависимости между модулями

```
:feature:* → :core:domain, :core:ui
:core:data  → :core:domain
:app        → :feature:*, :core:data, :core:domain, :core:ui
```

`:feature`-модули не зависят от `:core:data` напрямую — только через интерфейсы из `:core:domain`.

### Внутренняя структура feature-модуля

```
:feature:questions/
└── src/commonMain/kotlin/com/yahorshymanchyk/selectorassist/questions/
    ├── component/   # Decompose-компонент (интерфейс + реализация)
    ├── presentation/ # QuestionsState, QuestionsIntent, QuestionsViewModel
    └── ui/          # Compose-экраны
```

### Внутренняя структура :core:domain

```
:core:domain/src/commonMain/kotlin/com/yahorshymanchyk/selectorassist/domain/
├── model/          # Question, Entry, Tag
├── repository/     # QuestionRepository, EntryRepository (интерфейсы)
└── usecase/        # все UseCase'ы
```

### Внутренняя структура :core:data

```
:core:data/src/
├── commonMain/kotlin/.../data/
│   ├── repository/  # реализации репозиториев
│   └── mapper/      # SQLDelight entity → domain model
├── commonMain/sqldelight/  # .sq файлы
├── androidMain/    # AndroidDriver
└── iosMain/        # NativeDriver
```

---

## Доменные модели

```kotlin
data class Question(
    val id: Long,
    val title: String,       // "Уволиться или остаться?"
    val poleA: String,       // "Уйти"
    val poleB: String,       // "Остаться"
    val createdAt: Long,     // epoch ms
    val deadlineAt: Long,    // epoch ms
    val isCompleted: Boolean
)

data class Entry(
    val id: Long,
    val questionId: Long,
    val date: Long,          // epoch ms, только дата (time = 00:00:00)
    val sliderValue: Int,    // 1–5: 1=точно A, 3=нейтрально, 5=точно B
    val tags: List<Tag>,     // может быть пустым
    val comment: String?     // null если не заполнен
)

enum class Tag {
    // Ложные фильтры
    FEAR_OF_FUTURE,
    OPINION_OF_OTHERS,
    PAST_EXPERIENCE,
    EMOTIONS,
    // Опора
    MY_VALUES,
    FACTS_REASON,
    INTUITION
}
```

---

## Архитектура

### Слои и ответственность

```
UI (Compose)
  ↓ Intent
ViewModel (MVI)
  ↓ вызов
UseCase (:core:domain)
  ↓ вызов через интерфейс
Repository (интерфейс в :core:domain, реализация в :core:data)
  ↓
SQLDelight DB
```

Правило: каждый слой знает только о слое ниже. ViewModel не знает о SQLDelight. UseCase не знает о Compose.

### UseCase'ы (все в :core:domain/usecase/)

```kotlin
// Вопросы
GetActiveQuestionsUseCase    // () → Flow<List<Question>>
GetCompletedQuestionsUseCase // () → Flow<List<Question>>
CreateQuestionUseCase        // (title, poleA, poleB, deadlineAt) → Unit
DeleteQuestionUseCase        // (questionId) → Unit

// Записи
GetTodayEntryUseCase         // (questionId) → Flow<Entry?>
SaveEntryUseCase             // (questionId, sliderValue, tags, comment) → Unit

// Отчёт
GetQuestionStatsUseCase      // (questionId) → Flow<QuestionStats>
```

`QuestionStats` — отдельная data class в domain: распределение по слайдеру, частота тегов, аргументы по полюсам.

### Decompose — иерархия компонентов

```
RootComponent
├── BiometryComponent              — gate-экран при старте (если биометрия включена)
└── HomeComponent
    └── ChildStack<HomeChild>
        ├── QuestionsListComponent — главный экран, список вопросов
        ├── CreateQuestionComponent — создание новой дилеммы
        └── QuestionComponent      — контейнер для конкретного вопроса
            └── ChildStack<QuestionChild>
                ├── EntryComponent   — ежедневный ввод
                └── ReportComponent  — финальный отчёт
```

`RootComponent` переключает между `BiometryComponent` и `HomeComponent` через `Value<RootChild>`.
`HomeComponent` управляет основным стеком навигации через `ChildStack`.
`QuestionComponent` — вложенный `ChildStack` внутри конкретного вопроса.

Каждый компонент: интерфейс (что отдаёт UI) + реализация `Default` (держит дочерние компоненты и ViewModel).

### MVI-соглашения

Для каждого экрана три файла в `presentation/` внутри feature-модуля:
- `XxxState.kt` — data class, полное состояние UI
- `XxxIntent.kt` — sealed interface, все действия пользователя
- `XxxViewModel.kt` — принимает Intent, выдаёт `StateFlow<XxxState>`

---

## Правила написания кода

- Kotlin, идиоматично, без лишних абстракций
- Комментарии на английском
- Один файл — один класс (кроме мелких data class рядом с владельцем)
- Не создавать интерфейс там где одна реализация — только для тестов или expect/actual
- Все зависимости и версии — только через `gradle/libs.versions.toml`, никогда не хардкодить строками
- Не добавлять зависимости в `libs.versions.toml` без явного запроса
- Перед созданием файла проверять что он не существует
- После каждой завершённой задачи предлагать коммит в формате:
  `feat(scope): описание` / `fix(scope): описание` / `chore(scope): описание`

---

## MVP scope

**Входит:**
- Создание и удаление вопроса
- Ежедневный ввод (слайдер + теги + комментарий)
- Список активных и завершённых вопросов
- Финальный отчёт (статистика + паттерн + аргументы)
- Ежедневное уведомление-напоминание (Alarmee)
- Биометрическая защита входа (expect/actual)
- Полное удаление вопроса со всеми данными

**Не входит в MVP:**
- Аудиокомментарии
- Настраиваемые теги
- Экспорт данных
- Виджет на главный экран
- iPad / tablet layout

---

## Git-стратегия

```
main        — стабильный код, только merge из develop
develop     — текущая разработка
feature/    — новая функциональность
fix/        — исправление бага
chore/      — инфраструктура, зависимости, конфигурация
```

Прямые коммиты в `main` запрещены.
