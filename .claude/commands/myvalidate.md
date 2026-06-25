---
description: SelectorAssist — запускает ktlint + detekt + lintDebug + unit tests. С --ui добавляет snapshot тесты.
---

Запусти все четыре проверки для SelectorAssist. Запускай все даже если одна упала — собери все результаты перед отчётом.

## Команды (в этом порядке)

1. **ktlint** (самый быстрый — запускай первым):
   ```bash
   scripts/ktlint --relative '**/*.kt' '!**/build/**' --reporter=plain
   ```

2. **detekt**:
   ```bash
   ./gradlew detekt --no-configuration-cache
   ```

3. **lintDebug**:
   ```bash
   ./gradlew lintDebug --no-configuration-cache
   ```

4. **unit tests**:
   ```bash
   timeout 300 ./gradlew testDebugUnitTest --no-configuration-cache
   ```

Если передан аргумент `--ui`, дополнительно запусти:

5. ```bash
   ./gradlew verifyRoborazziDebug --no-configuration-cache
   ```
6. ```bash
   ./gradlew verifyPaparazziDebug --no-configuration-cache
   ```

## Формат вывода

После завершения всех команд — краткая сводка:

```
ktlint:  PASS
detekt:  FAIL
  - core/domain/src/.../Question.kt:42 — [rule] описание ошибки
  - feature/entry/src/.../EntryViewModel.kt:17 — [rule] описание ошибки
lint:    PASS
tests:   PASS

Итог: FAIL — исправь detekt перед коммитом.
```

Для ошибок: только путь к файлу, строка, сообщение. Без raw Gradle output.
