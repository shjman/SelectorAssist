---
description: SelectorAssist — запускает оркестратор для сложной задачи. Всё что после /mytask идёт в task.md.
---

Запусти оркестратор для выполнения сложной задачи.

## Шаг 1 — Архивируй старый контекст

Проверь существует ли `.claude/context/` и есть ли в ней файлы:
- task.md, research-report.md, consilium-report.md, plan.md, execution-report.md, review-result.md

Если файлы есть — автоматически перемести их в `.claude/context/archive/<timestamp>/`
где timestamp = текущие дата и время в формате `2026-06-24_14-30`.
Сообщи коротко: "Заархивировано в .claude/context/archive/2026-06-24_14-30/"

Если файлов нет — молча продолжай.

## Шаг 2 — Создай task.md

Создай директорию `.claude/context/` если не существует.
Создай `.claude/context/task.md` со следующей структурой:

```
# Task

<весь текст который пользователь написал после /mytask>

## HARD RULES
```

Затем прочитай секцию `## HARD RULES` из `CLAUDE.md` и добавь её содержимое в конец файла.

## Шаг 3 — Запусти оркестратор

Запусти агента orchestrator. Передай ему только:
"task.md создан в .claude/context/task.md. Начинай с Research."
