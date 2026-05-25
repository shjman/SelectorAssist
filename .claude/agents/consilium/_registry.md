# CONSILIUM Registry

## Всегда активные

- `kotlin-expert` — Kotlin idioms, Coroutines, Flow, KMP-специфика
- `android-architect` — MVI, Decompose, Koin, Clean Architecture, SQLDelight

## Опциональные

| Эксперт | Включать если |
|---------|---------------|
| `ui-designer` | новый экран, новый UI-компонент, изменения в AppColors/AppTypography, DESIGN_SYSTEM затронут |
| `kmp-expert` | задача затрагивает expect/actual, платформенный код (androidMain/iosMain), iOS-специфику |
| `android-security` | биометрия, локальное хранилище чувствительных данных, разрешения |
| `performance` | списки (LazyColumn), анимации, тяжёлые вычисления, SQLDelight-запросы по большим данным |

## Как добавить нового эксперта

1. Создать `.claude/agents/consilium/<name>.md`
2. Добавить строку в таблицу выше
3. Planner автоматически начнёт его использовать — менять planner.md не нужно
