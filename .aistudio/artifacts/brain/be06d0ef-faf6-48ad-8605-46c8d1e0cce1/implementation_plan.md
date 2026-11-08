# Theme Customization & SAF Custom Storage Directory

Support user-selectable theme styles (Dark YouTube Red, Light Mode, and Material You dynamic) and detailed storage configuration in Settings with a real Android System Storage Access Framework (SAF) folder picker allowing users to browse, pick, and persist any custom destination folder.

## User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed the following choices during the clarification phase:
> - **Theme Styles**: **Dark YouTube Red** (vibrant YouTube dark OLED), **Light Mode** (clean YouTube light aesthetics), and **Material You dynamic** (Android 12+ dynamic system palette).
> - **Custom Storage Path**: **System Storage Access Framework (Folder Picker)** with persistent URI permission (`Intent.ACTION_OPEN_DOCUMENT_TREE`), allowing the user to browse their device directories in the native file manager, verify the directory details, and save downloaded videos and MP3s directly to that chosen folder.

---

## 1. Overview & Core Concept

1. **Theme Switcher in Settings**:
   - Provide an interactive Theme Selection card in Settings with 3 distinct modes:
     - **Dark YouTube Red**: The signature high-contrast dark palette (`#0F0F0F` background, YouTube Red `#FF0033` accents).
     - **Light Mode**: Modern bright YouTube light aesthetic (`#F9F9F9` background, `#FFFFFF` cards, `#0F0F0F` typography, YouTube Red `#CC0000`).
     - **Material You Dynamic**: Dynamic theming utilizing `dynamicDarkColorScheme` / `dynamicLightColorScheme` on Android 12+ (API 31+) with fallback to Dark YouTube Red.
   - User selection will instantly apply across all tabs (`Downloader`, `Queue`, `Library`, `Settings`, dialogs, and top/bottom bars) and persist in app preferences.

2. **Storage Location Details & Custom SAF Directory Picker**:
   - Detail the default and active storage paths clearly in the Settings screen (e.g. `DCIM/Camera` for instant gallery detection, `Music/TubeGrab` for audio, `Movies/TubeGrab`, and `Custom Folder`).
   - Add a dedicated **Custom Directory** option with an interactive **"Choose Folder"** button.
   - When tapped, launches `ActivityResultContracts.OpenDocumentTree()` so the user can open their system file manager, navigate to any internal or SD card folder (e.g. `Downloads/MyVideos`, `DCIM/Vacation`, etc.), and grant access.
   - The app securely persists the directory URI permission (`takePersistableUriPermission`) and decodes a human-friendly folder path (e.g., `Internal Storage > Downloads > MyVideos`).
   - Downloads automatically target this custom folder when selected, saving directly via `DocumentFile` / `ContentResolver` or MediaStore while continuing to reflect in the Library.

---

## 2. User Experience & Visual Design

```
┌────────────────────────────────────────────────────────┐
│                   TubeGrab Settings                    │
├────────────────────────────────────────────────────────┤
│  🎨 App Theme & Styling                                │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ │
│  │ Dark YouTube  │ │  Light Mode   │ │  Material You │ │
│  │   [Active]    │ │               │ │   (Dynamic)   │ │
│  └───────────────┘ └───────────────┘ └───────────────┘ │
│                                                        │
│  📁 Download Storage & Destination                     │
│  ◉ DCIM / Camera (Appears in Gallery & Photos)         │
│  ○ Music / TubeGrab (Standard Audio Folder)            │
│  ○ Movies / TubeGrab (Dedicated Video Folder)          │
│  ◉ Custom Directory (Selected via File Explorer)       │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Current: /storage/emulated/0/Downloads/YouTube   │  │
│  │ [ 📂 Browse & Pick Folder ]                      │  │
│  └──────────────────────────────────────────────────┘  │
│  ✓ Persistent Write Access Granted                     │
└────────────────────────────────────────────────────────┘
```

- **Interactive Theme Preview**:
  - Each theme option shows an illustrative miniature preview badge with primary accent and surface colors.
  - Selecting an option triggers smooth color transitions across all screens.
- **Detailed Storage Info**:
  - Displays storage explanation and gallery indexing notice.
  - When custom folder is selected, displays the decoded tree URI or directory name with a folder icon, verification chip ("Verified & Ready"), and quick re-select option.

---

## 3. Key Product Decisions & Trade-Offs

- **Storage Access Framework (SAF) vs Raw File Path**:
  - *Chosen Approach*: `ActivityResultContracts.OpenDocumentTree()` with `takePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION)`.
  - *Why*: Modern Android (API 30+) strictly limits direct raw file path writes outside scoped storage. SAF is the officially supported, zero-crash mechanism that allows users to pick any folder on their device and write files cleanly without permission denials.
- **Theme State Management**:
  - *Chosen Approach*: `AppThemeMode` enum (`DARK_YOUTUBE`, `LIGHT`, `MATERIAL_YOU`) stored in `DownloaderUiState` and injected into `TubeGrabTheme` at root `MainActivity`.
  - *Why*: Allows instantaneous recomposition without restarting the activity, supporting seamless user customization.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│   • Holds OpenDocumentTree activity result launcher                    │
│   • Observes uiState.appThemeMode -> passes into TubeGrabTheme         │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           TubeGrabViewModel                            │
│   • appThemeMode: AppThemeMode (DARK_YOUTUBE, LIGHT, MATERIAL_YOU)     │
│   • customStorageUri: Uri?, customStoragePathDisplay: String?          │
│   • setAppTheme(theme: AppThemeMode)                                   │
│   • setCustomStorageUri(uri: Uri, displayPath: String)                 │
└───────────────────┬─────────────────────────────────┬──────────────────┘
                    │                                 │
                    ▼                                 ▼
┌───────────────────────────────┐ ┌──────────────────────────────────────┐
│        SettingsScreen         │ │          YouTubeRepository           │
│  • Theme selector cards       │ │  • installFileToMediaStore           │
│  • Storage details & presets  │ │  • installFileToCustomSafTree        │
│  • "Browse Folder" launcher   │ │    (writes to DocumentFile tree)     │
└───────────────────────────────┘ └──────────────────────────────────────┘
```

### Components to Update:
1. **`com.example.ui.theme.Theme.kt`**:
   - Add `AppThemeMode` enum (`DARK_YOUTUBE`, `LIGHT`, `MATERIAL_YOU`).
   - Enhance `TubeGrabTheme` to accept `appThemeMode: AppThemeMode` and support `dynamicDarkColorScheme`/`dynamicLightColorScheme` when on Android 12+ (Build.VERSION_CODES.S).
2. **`com.example.ui.TubeGrabViewModel.kt`**:
   - Add `appThemeMode: AppThemeMode`, `customStorageUri: String?`, and `customStorageDisplayPath: String?` to `DownloaderUiState`.
   - Add methods `setAppTheme(theme: AppThemeMode)` and `setCustomFolder(uri: Uri, displayPath: String)`.
3. **`com.example.ui.screens.SettingsScreen.kt`**:
   - Add Theme Selection section with clickable cards and active indicator.
   - Add Storage Details card with preset breakdown, custom directory path display, and `onPickFolder` action.
4. **`com.example.MainActivity.kt`**:
   - Set up `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree())` to grant persistable URI permissions and pass the picked folder to ViewModel.
   - Wire `TubeGrabTheme(themeMode = uiState.appThemeMode)` at top level.
5. **`com.example.data.repository.YouTubeRepository.kt`**:
   - Support writing to custom SAF tree URI using `DocumentFile.fromTreeUri(context, customTreeUri)` when `StorageLocation.CUSTOM` is active.
