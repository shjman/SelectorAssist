# CONSILIUM Registry

## Always active

- `kotlin-expert` — Kotlin idioms, Coroutines, Flow, KMP specifics
- `android-architect` — MVI, Decompose, Koin, Clean Architecture, SQLDelight

## Optional

| Expert | Include when |
|--------|-------------|
| `ui-designer` | new screen, new UI component, changes to AppColors/AppTypography, DESIGN_SYSTEM affected |
| `kmp-expert` | task touches expect/actual, platform code (androidMain/iosMain), iOS specifics |
| `android-security` | biometry, local storage of sensitive data, permissions |
| `performance` | lists (LazyColumn), animations, heavy computations, SQLDelight queries on large datasets |

## How to add a new expert

1. Create `.claude/agents/consilium/<name>.md`
2. Add a row to the table above
3. Planner will automatically start using it — no need to change planner.md
