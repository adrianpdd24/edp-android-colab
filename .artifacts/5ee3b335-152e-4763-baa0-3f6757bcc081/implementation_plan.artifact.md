# UI Redesign - Aesthetic & Modern Material 3 Design

Enhance the visual appeal and UX of LiceoFieldKit using modern Material 3 design principles, including refined typography, elevated cards with rounded corners, icons, better color contrast, top app bar header, and polished button/preview styling.

## User Review Required

> [!IMPORTANT]
> All functional logic and GIVEN specifications remain fully intact while enhancing visual presentation (cards, spacing, icons, typography, rounded corners, and top app bar).

## Open Questions
- None.

## Proposed Changes

### [Component Name]

#### [MODIFY] [MainActivity.kt](file:///Users/kaijo0126/Documents/LDCU CODES/dimasuhid/app/src/main/java/edu/liceo/fieldkit/MainActivity.kt)
- Add a modern Material 3 `TopAppBar` (`LiceoFieldKit` with subtitle or styled title).
- Wrap screen content in a clean scaffold with a pleasant background color and comfortable outer padding (`20.dp`).

#### [MODIFY] [LevelCard.kt](file:///Users/kaijo0126/Documents/LDCU CODES/dimasuhid/app/src/main/java/edu/liceo/fieldkit/ui/LevelCard.kt)
- Use `ElevatedCard` with custom tonal colors and rounded corners.
- Add sensor icon (`Icons.Default.Explore` or `Sensors`), better typography for accelerometer values (`mono` or stylized font style), and a pill-shaped status indicator badge for "LEVEL ✓" / "Tilted, adjust".

#### [MODIFY] [CameraCard.kt](file:///Users/kaijo0126/Documents/LDCU CODES/dimasuhid/app/src/main/java/edu/liceo/fieldkit/ui/CameraCard.kt)
- Use `ElevatedCard`.
- Add camera icon header.
- Round corners of `CameraPreview` (`Clip(RoundedCornerShape(16.dp))`).
- Style buttons with icons (`Icons.Default.CameraAlt`, `Icons.Default.FlashlightOn`) and rounded shapes.
- Round corners and add shadow/border to the saved photo thumbnail.

#### [MODIFY] [LocationCard.kt](file:///Users/kaijo0126/Documents/LDCU CODES/dimasuhid/app/src/main/java/edu/liceo/fieldkit/ui/LocationCard.kt)
- Use `ElevatedCard`.
- Add location icon header.
- Highlight location access status with a badge/chip.
- Style tag button with `Icons.Default.LocationOn`.

#### [MODIFY] [PermissionGate.kt](file:///Users/kaijo0126/Documents/LDCU CODES/dimasuhid/app/src/main/java/edu/liceo/fieldkit/ui/PermissionGate.kt)
- Improve styling of rationale and blocked state text/buttons with friendly warning icons and card backgrounds.

## Verification Plan

### Automated Tests
- Run Gradle build (`gradle_build("app:assembleDebug")`) to verify successful compilation.

### Manual Verification
- Review UI structure and verify all card elements, permissions, sensors, camera preview, and location tagging remain fully operational.
