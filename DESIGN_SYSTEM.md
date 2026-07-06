# SelectorAssist — Design System

Dark-only theme. All colors via `AppColors`, all type via `AppTypography`.  
No Material3 default theming overrides — everything is explicit.

---

## AppColors

Full reference: `core/ui/src/commonMain/kotlin/.../ui/theme/AppColors.kt`

| Token | Hex | Used for |
|-------|-----|----------|
| `Background` | `#1A1A1A` | Screen background (`Modifier.background(AppColors.Background)`) |
| `Surface` | `#252525` | Cards, bottom bar, header sections |
| `TextPrimary` | `#FFFFFF` | Main content text, active input text |
| `TextSecondary` | `#8E8E93` | Labels, hints, secondary info |
| `TextTertiary` | `#48484A` | Placeholders, disabled text |
| `PoleA` | `#64D2FF` | Left pole label + slider track start |
| `PoleB` | `#FF6B9D` | Right pole label + slider track end |
| `PendingIndicator` | `#FF9F0A` | "Entry needed today" dot on question card |
| `PendingBorder` | `#6B4C00` | Border for pending question card |
| `ProgressTrack` | `#3A3A3C` | Background of progress bars |
| `ProgressFill` | `#FFFFFF` | Fill of progress bars |
| `Divider` | `#38383A` | Horizontal dividers inside cards |
| `Chevron` | `#636366` | `›` navigation arrows |
| `InputField` | `#2C2C2E` | Text field background, unselected chip |
| `InputFieldSelected` | `#3A3A3C` | Selected chip background |
| `InputFieldSelectedBorder` | `#636366` | Border on selected chip |
| `TagGroupNoise` | `#FF9500` | Bullet dot — "False filters" section |
| `TagGroupHealthy` | `#30D158` | Bullet dot — "Support" section |

**Rule:** Adding a new color = add to `AppColors` with `@file:Suppress("MagicNumber")` already present.  
Never use `Color(0xFF...)` inline in screens.

---

## AppTypography

Full reference: `core/ui/src/commonMain/kotlin/.../ui/theme/AppTypography.kt`

| Style | Size / Weight / Line height | Used for |
|-------|-----------------------------|----------|
| `headlineLarge` | 28sp Bold / 34sp | — (reserved) |
| `titleLarge` | 18sp SemiBold / 24sp | Screen titles |
| `bodyMedium` | 14sp Regular / 20sp | Body text, list items |
| `labelSmall` | 12sp Regular / 16sp | Captions, helper text |

Most screens use inline `fontSize` / `fontWeight` directly for fine-grained control.  
Typography styles are a baseline — not enforced everywhere yet.

---

## Platform-adaptive UI (`isAndroid`)

`core/ui/src/commonMain/kotlin/.../ui/theme/Platform.kt` — `expect val isAndroid: Boolean` (actuals: android / ios / wasmJs).

Use it for small platform-idiom differences, never for logic:

- **Back navigation:** `BackButton` from `core:ui` renders a Material `ArrowBack` icon on Android, a `‹` text chevron elsewhere. Always use the shared component, don't inline.
- **Header sizing:** Android headers use `start = 4.dp`, `fontSize = 22.sp`; iOS/web use `start = 8.dp`, `fontSize = 20.sp`.
- **Primary action placement:** Android — FAB (e.g. create question); iOS/web — inline button in content.
- **Accents:** Android may use `AppColors.PoleA` + `FontWeight.Medium` where iOS uses `TextSecondary` + `Normal` (see SettingsScreen).

Shared components in `core:ui/components`: `BackButton`, `SettingsIconButton` (both platform-adaptive via `isAndroid`).

---

## Screen skeleton

```kotlin
/**
 * ARCH: Standard screen structure — fixed header + scrollable body + fixed bottom action
 */
@Composable
fun XxxScreen(component: XxxComponent) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        XxxHeader(onBack = component::onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            // content
            Spacer(Modifier.height(16.dp))
        }

        XxxSubmitButton(
            enabled = state.canSubmit,
            onClick = { component.onIntent(XxxIntent.Submit) },
        )
    }
}
```

---

## UI components

### Card (Surface container)

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(AppColors.Surface)
        .padding(horizontal = 16.dp, vertical = 20.dp),
) {
    content()
}
```

### Divider (inside card)

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .height(1.dp)
        .background(AppColors.Divider),
)
```

### Section label

```kotlin
Text(text = label, color = AppColors.TextSecondary, fontSize = 13.sp)
```

### Text field (BasicTextField)

```kotlin
/**
 * ARCH: BasicTextField instead of Material3 TextField
 * WHY: Full control over styling without fighting M3 defaults in a custom dark theme.
 */
val textStyle = remember { TextStyle(color = AppColors.TextPrimary, fontSize = 17.sp) }

BasicTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(AppColors.InputField)
        .padding(horizontal = 14.dp, vertical = 14.dp),
    textStyle = textStyle,
    cursorBrush = SolidColor(AppColors.TextPrimary),
    decorationBox = { innerTextField ->
        Box {
            if (value.isEmpty()) {
                Text(text = placeholder, color = AppColors.TextTertiary, fontSize = 17.sp)
            }
            innerTextField()
        }
    },
)
```

### Tag chip (selectable)

```kotlin
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(if (isSelected) AppColors.InputFieldSelected else AppColors.InputField)
        .then(
            if (isSelected) Modifier.border(1.dp, AppColors.InputFieldSelectedBorder, RoundedCornerShape(20.dp))
            else Modifier
        )
        .clickable(onClick = onClick)
        .padding(horizontal = 12.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center,
) {
    Text(
        text = label,
        color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
        fontSize = 14.sp,
    )
}
```

Use `FlowRow` from `androidx.compose.foundation.layout` (`@OptIn(ExperimentalLayoutApi::class)`)  
with `horizontalArrangement = Arrangement.spacedBy(8.dp)` and `verticalArrangement = Arrangement.spacedBy(8.dp)`.

### Polar slider (PoleA ↔ PoleB gradient track)

```kotlin
/**
 * ARCH: Gradient track via layered Box + transparent-track Slider
 * WHY: Compose Slider has no native gradient track support.
 *      Gradient Box sits behind; Slider thumb floats on top with transparent track.
 */
Box(modifier = Modifier.fillMaxWidth()) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .align(Alignment.Center)
            .padding(horizontal = 10.dp)   // compensates thumb radius
            .clip(RoundedCornerShape(2.dp))
            .background(Brush.horizontalGradient(listOf(AppColors.PoleA, AppColors.PoleB))),
    )
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent,
        ),
    )
}
```

Slider value: `Float 0.0..1.0` in UI → stored as `Int 0..10` via `(value * 10).toInt()`.

### Bottom action button

```kotlin
private const val BUTTON_HEIGHT = 72

Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(BUTTON_HEIGHT.dp)
        .background(AppColors.Surface)
        .clickable(enabled = canSubmit, onClick = onClick),
    contentAlignment = Alignment.Center,
) {
    Text(
        text = label,
        color = if (canSubmit) AppColors.TextPrimary else AppColors.TextTertiary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
    )
}
```

### Screen header with back

Back arrow — always the shared `BackButton` from `core:ui` (platform-adaptive, see the `isAndroid` section above).

```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(start = if (isAndroid) 4.dp else 8.dp, end = 20.dp, top = 56.dp, bottom = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    BackButton(onClick = onBack)
    Text(
        text = screenTitle,
        color = AppColors.TextPrimary,
        fontSize = if (isAndroid) 22.sp else 20.sp,
        fontWeight = FontWeight.SemiBold,
    )
}
```

---

## UI rules

- All colors → `AppColors`. Never `Color(0xFF...)` inline.
- All magic numbers → named `private const val`. File-level `@file:Suppress("MagicNumber")` with comment when a block of layout constants makes it warranted.
- No business logic in `@Composable` functions — only state reading + intent dispatching.
- No `ViewModel`, `remember { mutableStateOf() }`, or coroutines inside screens — state lives in `XxxState`.  
  Exception: purely local ephemeral UI state with no domain meaning (e.g., `rememberScrollState()`).
- Shapes, paddings, font sizes: inline constants at file top if reused within the file; in `AppColors` / `AppTypography` only if shared across modules.
- `@Suppress` only with a one-line justification comment.
