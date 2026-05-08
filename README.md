# SelectorAssist

A Kotlin Multiplatform app for tracking binary dilemmas. The user defines two poles and an observation period — each day they log a slider position, tags, and an optional comment. At the end of the period, a report shows patterns and tendencies.

Targets: Android (Google Play) + iOS (App Store) + Web (GitHub Pages). Fully shared UI across all platforms.

**Live demo:** https://shjman.github.io/SelectorAssist/

---

## Features

- **Binary dilemma** — define a question with two poles (e.g. "Quit / Stay") and a deadline
- **Daily entry** — slider between the two poles, optional tag classification, optional free-text comment
- **Two tag groups** — *Noise* (false filters: fear, guilt, impulses…) and *Healthy* (grounded reasons: values, long-term goals…)
- **Upsert behaviour** — re-opening a day's entry pre-fills with the saved values; saving again updates in place
- **Final report** — tendency bars (% per pole), tag group influence, and all user comments split by pole
- **Biometric lock** — optional Face ID / Fingerprint gate on app open *(planned)*
- **Local-only** — no network, no analytics, no sync; all data stays on device

---

## Tech Stack

| Area | Library / Version |
|------|--------------------|
| Language | Kotlin 2.3.20 |
| UI | Compose Multiplatform 1.10.3 (commonMain, Android + iOS + Web) |
| Material | JetBrains Material3 1.10.0-alpha05 |
| Navigation | Decompose 3.5.0 (ChildStack) |
| Architecture | MVI + plain-class ViewModel + Decompose Components |
| DI | Koin 4.2.1 (KMP) |
| Database | SQLDelight 2.3.2 |
| Async | Kotlinx Coroutines 1.9.0 + StateFlow |
| Notifications | Alarmee 2.6.0 (commonMain) |
| Biometrics | AndroidX Biometric 1.1.0 / LocalAuthentication (expect/actual) |
| Static analysis | Detekt 1.23.7 |
| Build | AGP 8.11.2, minSdk 28, targetSdk 36, JVM 17 |
| CI / CD | GitHub Actions → GitHub Pages |

---

## Project Structure

```
.
├── core/
│   ├── domain/                     # Pure Kotlin — no Android/iOS deps
│   │   └── commonMain/
│   │       ├── model/              # Question, Entry, Tag, TagGroup, *Summary, QuestionStats
│   │       ├── repository/         # QuestionRepository, EntryRepository (interfaces)
│   │       └── usecase/            # GetActive*, GetCompleted*, Create*, Save*, GetTodayEntry…
│   │
│   ├── data/                       # SQLDelight + repository implementations
│   │   └── commonMain/
│   │       ├── db/                 # AppDatabase.sq schema + generated queries
│   │       ├── repository/         # QuestionRepositoryImpl, EntryRepositoryImpl
│   │       └── mapper/             # Entries/Questions → domain model
│   │
│   └── ui/                         # Shared Compose design tokens
│       └── commonMain/theme/
│           ├── AppColors.kt
│           ├── AppTypography.kt
│           └── AppTheme.kt
│
├── feature/
│   ├── questions/                  # QuestionsListScreen + CreateQuestionScreen
│   │   └── commonMain/
│   │       ├── component/          # QuestionsListComponent, CreateQuestionComponent
│   │       ├── presentation/       # *State, *Intent, *ViewModel
│   │       └── ui/                 # *Screen composables
│   │
│   ├── entry/                      # Daily entry (slider + tags + comment)
│   │   └── commonMain/             # same structure: component / presentation / ui
│   │
│   └── report/                     # Final report — tendency, tag influence, arguments
│
└── composeApp/                     # App entry point
    ├── commonMain/
    │   ├── RootComponent.kt        # Top-level Decompose component
    │   ├── DefaultRootComponent.kt # KoinComponent — injects use cases
    │   ├── HomeComponent.kt        # ChildStack interface
    │   ├── DefaultHomeComponent.kt # Navigation logic
    │   ├── RootContent.kt          # Root @Composable — routes stack to screens
    │   └── di/AppModule.kt         # dataModule + domainModule
    ├── androidMain/
    │   ├── MainActivity.kt
    │   ├── SelectorAssistApp.kt    # Koin init
    │   └── di/AndroidPlatformModule.kt
    ├── iosMain/
    │   ├── MainViewController.kt   # Koin init + ComposeUIViewController
    │   └── di/IosPlatformModule.kt
    └── wasmJsMain/                 # Web entry point
        └── main.kt                 # ComposeViewport
```

---

## Architecture

Four modules with a strict dependency graph:

```
:core:domain   (pure Kotlin — no platform deps)
      ↑                ↑
:core:data      :core:ui
      ↑                ↑
         :feature:*
              ↑
          :composeApp
```

**Rule:** `:feature:*` may only depend on `:core:domain` and `:core:ui`. Never on `:core:data`.

**Layer flow:**
```
Screen (@Composable)
  └─ onIntent() ──► Component (Decompose, lifecycle)
                        └─ ViewModel (plain class, MVI)
                                └─ UseCase
                                      └─ Repository interface
                                              └─ RepositoryImpl (SQLDelight)
```

**Component pattern:**
```
XxxComponent (interface)          — Value<XxxState> + onIntent()
DefaultXxxComponent               — owns CoroutineScope, bridges StateFlow → MutableValue,
                                    cancels scope on lifecycle.doOnDestroy
XxxViewModel (plain class)        — pure business logic, scope injected from Component
```

---

## Database Schema

**`questions`**

| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER | Primary key, auto-generated |
| title | TEXT | The dilemma as a question |
| pole_a | TEXT | Left pole label |
| pole_b | TEXT | Right pole label |
| created_at | INTEGER | Unix timestamp ms |
| deadline_at | INTEGER | Unix timestamp ms |
| is_completed | INTEGER | 0 / 1 |

**`entries`**

| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER | Primary key, auto-generated |
| question_id | INTEGER | FK → questions.id |
| date | INTEGER | Day at midnight UTC ms (one entry per day per question) |
| slider_value | INTEGER | 0–10 (0 = pole A, 10 = pole B) |
| comment | TEXT | Nullable |

**`entry_tags`**

| Column | Type | Notes |
|--------|------|-------|
| entry_id | INTEGER | FK → entries.id |
| tag | TEXT | `Tag` enum name (e.g. `FEAR_OF_FUTURE`) |

---

## Build Commands

```bash
# Android
./gradlew :composeApp:assembleDebug          # build debug APK
./gradlew :composeApp:assembleRelease        # build release APK
./gradlew :composeApp:installDebug           # build + install on device/emulator

# Web
./gradlew :composeApp:wasmJsBrowserDistribution   # build → composeApp/build/dist/wasmJs/productionExecutable/

# Static analysis (must be clean before commit)
./gradlew lintDebug --no-configuration-cache
./gradlew detekt --no-configuration-cache
```

**iOS:** open `iosApp/iosApp.xcodeproj` in Xcode and run, or use the Android Studio iOS run configuration.

---

## CI / CD

Push to `main` triggers `.github/workflows/web-deploy.yml`:

1. Build `wasmJsBrowserDistribution`
2. Deploy `composeApp/build/dist/wasmJs/productionExecutable/` to GitHub Pages

Result is live at https://shjman.github.io/SelectorAssist/

---

## Navigation

Current Decompose stack:

```
RootComponent
└── HomeComponent (ChildStack)
    ├── QuestionsListComponent   ✅
    ├── CreateQuestionComponent  ✅
    ├── EntryComponent           ✅
    └── ReportComponent          ✅

Planned:
    └── QuestionComponent (nested ChildStack)
        ├── EntryComponent
        └── ReportComponent
```
