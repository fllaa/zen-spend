# Kotlin Android Boilerplate

Reusable Kotlin-first Android boilerplate for a medium-scale production app.

## Stack

- Package: `com.flla.zenspend`
- UI: Jetpack Compose only, Material 3
- Architecture: MVVM, local-first repositories, feature navigation graphs
- DI: Hilt
- Network: Retrofit, OkHttp, Kotlinx Serialization
- Local storage: Room and encrypted-token DataStore preferences
- Testing: JUnit, MockK, Turbine, Compose UI testing
- Quality: ktlint and detekt
- CI: GitHub Actions for build, unit tests, ktlint, and detekt

## Module Responsibilities

- `app`: application class, app-level DI, root Compose entry point, theme, splash/session routing, and navigation wiring.
- `feature:*`: screens, ViewModels, UI state, UI events, and feature navigation.
- `core:designsystem`: Material 3 theme, colors, typography, spacing, buttons, fields, app bars, and state views.
- `core:ui`: shared Compose helpers.
- `core:domain`: repository interfaces and use cases.
- `core:model`: shared domain models.
- `core:network`: Retrofit APIs, OkHttp interceptors/authenticator, DTOs, network mapping, demo API interceptor, and repository implementations.
- `core:database`: Room database, DAO, entities, migrations, mappers, and local data source.
- `core:datastore`: DataStore-backed auth tokens and user preferences. Tokens are encrypted with Android Keystore before being persisted.
- `core:common`: result wrappers, app errors, dispatchers, constants, and cross-cutting utilities.
- `core:testing`: fake repositories, sample data, dispatcher rule, and test helpers.
- `build-logic`: convention plugins for Android app/library, Compose, Hilt, JVM library, and quality tooling.

## Sample Flows

The app starts at `splash`, observes `SessionRepository.sessionState`, then routes to:

- `auth/login` for unauthenticated or expired sessions.
- `auth/register` from login.
- `main_graph/home` for authenticated sessions.
- `profile` and `settings` from Home.

Logout clears tokens and the cached user. Session state changes are observed at the app shell, so the navigation stack returns to auth automatically.

## Local-First Sync

UI reads from local sources with `Flow`.

- `HomeViewModel` and `ProfileViewModel` observe `UserRepository.observeCurrentUser()`.
- `OfflineFirstUserRepository` exposes Room as the source of truth.
- `refreshCurrentUser()` fetches the remote user and updates Room.
- Network failure returns `AppResult.Failure`, but cached Room reads continue to flow to UI.
- DTO, entity, and domain mapping are separated in network/database mapper files.

## Authentication

`DefaultAuthRepository` handles login, register, and logout.

- Successful login/register saves access and refresh tokens through `AuthTokenDataSource`.
- `AccessTokenInterceptor` attaches the access token.
- `TokenRefreshAuthenticator` uses a separate refresh Retrofit client to refresh tokens on `401`.
- Failed refresh marks the session expired and clears stored tokens.
- `FakeDemoInterceptor` serves demo responses for `https://demo.example.local/`, so the scaffold can run without a backend.

For production, replace `FakeDemoInterceptor`, point `NetworkModule.provideBaseUrl()` at your API, and review token storage requirements for your threat model.

## Run

This scaffold expects Gradle 8.9 and JDK 17.

```bash
gradle assembleDebug
```

## Tests And Quality

```bash
gradle testDebugUnitTest
gradle connectedDebugAndroidTest
gradle ktlintCheck
gradle detekt
gradle build
```

Included examples:

- `feature:auth`: `LoginViewModelTest`
- `feature:auth`: `LoginScreenTest`
- `core:network`: `OfflineFirstUserRepositoryTest`
- `core:database`: `UserDaoTest`

## Add A Feature Module

1. Create `feature/<name>`.
2. Add `include(":feature:<name>")` to `settings.gradle.kts`.
3. Apply `com.flla.zenspend.android.library`, `com.flla.zenspend.android.compose`, `com.flla.zenspend.android.hilt`, and `com.flla.zenspend.quality`.
4. Depend on `core:domain`, `core:model`, `core:designsystem`, and `core:ui` as needed.
5. Add `<Name>Routes`, `<Name>Navigation`, screens, UI state, and ViewModels.
6. Wire the feature graph from `ZenSpendApp`.

Keep shared contracts in `core:domain` and shared models in `core:model`. Keep feature-only presentation state inside the feature module.
