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

## Version Catalogs

Все зависимости и версии — только через `gradle/libs.versions.toml`.
Никогда не прописывать версии строками прямо в `build.gradle.kts`.

Правильно:
```toml
[versions]
alarmee = "2.0.0"

[libraries]
alarmee = { module = "io.github.tweener:alarmee", version.ref = "alarmee" }
```

Неправильно:
```kotlin
implementation("io.github.tweener:alarmee:2.0.0")
```

---

## Структура модулей

```
composeApp/
└── src/
    ├── commonMain/
    │   ├── kotlin/com/yahorshymanchyk/selectorassist/
    │   │   ├── data/           # SQLDelight-репозитории, маперы
    │   │   ├── domain/         # модели, use cases, интерфейсы репозиториев
    │   │   ├── presentation/   # ViewModel, MVI State/Intent/Effect
    │   │   ├── ui/             # Compose экраны и компоненты
    │   │   ├── navigation/     # Decompose RootComponent и дерево компонентов
    │   │   └── di/             # Koin modules
    │   └── sqldelight/         # .sq файлы схемы БД
    ├── androidMain/
    │   └── kotlin/com/yahorshymanchyk/selectorassist/
    │       ├── BiometryManager.android.kt
    │       └── MainActivity.kt (уже существует)
    └── iosMain/
        └── kotlin/com/yahorshymanchyk/selectorassist/
            └── BiometryManager.ios.kt
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

## MVI-соглашения

Для каждого экрана три файла в `presentation/`:
- `XxxState.kt` — data class, полное состояние UI
- `XxxIntent.kt` — sealed interface, все действия пользователя
- `XxxViewModel.kt` — принимает Intent, выдаёт StateFlow<XxxState>

---

## Правила написания кода

- Kotlin, идиоматично, без лишних абстракций
- Комментарии на английском
- Один файл — один класс (кроме мелких data class рядом с владельцем)
- Не создавать интерфейс там где одна реализация — только для тестов или expect/actual
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
