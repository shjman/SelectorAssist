---
description: SelectorAssist — архивирует context-файлы оркестратора перед новой задачей.
---

Управляй файлами контекста оркестратора в `.claude/context/`.

## Файлы под управлением (только эти — больше ничего не трогай)

- `.claude/context/task.md`
- `.claude/context/research-report.md`
- `.claude/context/consilium-report.md`
- `.claude/context/plan.md`
- `.claude/context/execution-report.md`
- `.claude/context/review-result.md`

## Процесс

### Шаг 1 — Проверь что есть

Проверь какие из файлов выше существуют. Если ни одного — сообщи:
```
Context уже чист. Нечего архивировать.
```
и заверши.

### Шаг 2 — Покажи список и спроси

Выведи список существующих файлов и спроси подтверждение:
```
Найдены файлы контекста:
  - task.md
  - plan.md
  - execution-report.md

Заархивировать в .claude/context/archive/YYYY-MM-DD_HH-mm/ ? (да/нет)
```

### Шаг 3 — Архивируй

При подтверждении:
1. Создай папку `.claude/context/archive/<timestamp>/` где timestamp = текущие дата и время в формате `2026-06-24_14-30`
2. Перемести все найденные файлы в эту папку
3. Сообщи результат:
```
Заархивировано в .claude/context/archive/2026-06-24_14-30/
  - task.md
  - plan.md
  - execution-report.md

Context чист. Готов к новой задаче.
```

При отказе — ничего не делай, сообщи "Отменено."
