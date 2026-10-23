# Folder Station

Folder Station is a native Android application that enables users to create beautiful, customizable home-screen folder widgets for organizing applications and games.

## Visual Identity
- Inspired by modern dark navy and glowing folder accents.
- Dynamic glowing gradients: Electric Blue (`#388BFD`), Neon Cyan (`#00E5FF`), and Violet (`#8B5CF6`).
- Full support for English and Arabic (RTL).

## Features
- **Real Native Android Home-Screen Widgets**:
  - Genuine Android `AppWidgetProvider` and `RemoteViewsService` + `RemoteViewsFactory` collection support.
  - Distinct `PendingIntent` for each app icon inside the widget to launch the target app directly with Android 12+ background activity compatibility.
  - Launcher configuration activity (`FolderConfigureActivity`) when adding widgets from the home screen.
  - Native programmatic widget pinning (`requestPinAppWidget`) on Android 8.0+.
  - Responsive resizing: handles launcher resize events smoothly.
- **Multiple Widget Grid Presets**:
  - 1×1, 2×2, 2×3, 3×2, 3×3, 4×2, 4×3, 4×4, 1×5 vertical list, and custom column/row combinations.
- **Rich Folder Appearance Customization**:
  - Shapes: Rounded rectangle, square, large rounded square, circle, minimal transparent glass, list, and bordered.
  - Full color customization: background color, gradient start/end, opacity, corner radius, border color, and border thickness.
  - Typography: folder title toggle, title color/size, app label visibility, and label color/size.
  - Live interactive preview in the editor updating in real time.
- **Installed Application and Game Picker**:
  - Automatic discovery of installed launchable apps via `<queries>` package visibility.
  - Fast search by application name or package.
  - Game and system app categorization.
  - In-memory `LruCache` for app icons for smooth 60fps scrolling and fast widget rendering.
- **Local Persistence & Data Safety**:
  - Offline-first Room Database (`AppDatabase`, `FolderDao`).
  - No internet connection or accounts required.
  - JSON-based backup export and import.

## Building and Testing
```bash
# Compile and build the debug APK:
gradle assembleDebug

# Run unit and Robolectric tests:
gradle :app:testDebugUnitTest
```
