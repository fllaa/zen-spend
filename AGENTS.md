# AGENTS.md

Guidance for future AI agents working in this repository.

## Project Shape

This is a Kotlin-first, Compose-only Android boilerplate for package `com.flla.zenspend`.

The repository is intentionally multi-module:

- `app`: app entry point, Hilt application, root Compose setup, app-level DI, theme selection, splash/session routing, and navigation wiring only.
- `feature:*`: feature screens, ViewModels, UI state, UI events, and feature navigation graphs.
- `core:domain`: repository interfaces and use cases.
- `core:model`: shared domain models.
- `core:network`: Retrofit, OkHttp, interceptors/authenticator, DTOs, network mappers, demo API interceptor, and repository implementations.
- `core:database`: Room database, DAOs, entities, migrations, entity/domain mappers, and local data sources.
- `core:datastore`: DataStore preferences for auth tokens and user preferences.
- `core:designsystem`: Material 3 theme, colors, typography, spacing, reusable components, and state views.
- `core:ui`: shared Compose helpers.
- `core:common`: result wrappers, error types, constants, dispatchers, and small shared utilities.
- `core:testing`: fake repositories, sample data, dispatcher rules, and test utilities.
- `build-logic`: Gradle convention plugins.

Keep these boundaries intact. Do not move repository interfaces into feature modules or put app-level navigation into features.

## Architecture Rules

- Use MVVM for feature presentation.
- UI observes `Flow`/`StateFlow`; ViewModels expose immutable state.
- Keep DTO, entity, and domain mapping separated.
- Keep Room/DataStore as observable local sources of truth.
- Network refreshes should update local storage and must not break cached reads.
- Auth session state should flow through `SessionRepository`.
- Token attachment and refresh belong in OkHttp components under `core:network`.
- Prefer small use cases over feature modules calling repositories directly.
- Use Hilt constructor injection for ViewModels, use cases, repositories, and data sources.

## UI Rules

- Jetpack Compose only. Do not add XML layouts.
- Use Material 3 and components from `core:designsystem` before creating feature-local UI.
- Shared Compose helpers belong in `core:ui`.
- Feature screens should be stateless where practical, with route composables collecting ViewModel state.
- Keep navigation route constants or graph helpers inside the owning feature module.

## Build And Quality

Use the Gradle wrapper when available:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew ktlintCheck
./gradlew detekt
./gradlew build
```

For Android instrumentation tests:

```bash
./gradlew connectedDebugAndroidTest
```

If a Gradle command needs network/dependency access and fails due to sandboxing, request escalation rather than working around dependency resolution.

## Adding A Feature

1. Create `feature:<name>`.
2. Add it to `settings.gradle.kts`.
3. Apply the existing convention plugins:
   - `com.flla.zenspend.android.library`
   - `com.flla.zenspend.android.compose`
   - `com.flla.zenspend.android.hilt`
   - `com.flla.zenspend.quality`
4. Depend only on the core modules the feature needs.
5. Add route constants, navigation extension, ViewModel, UI state, and screen composables.
6. Wire the feature graph from `app` only.

## Testing Expectations

- Add or update unit tests when changing ViewModel/use case/repository behavior.
- Add Room DAO tests for schema or query changes.
- Add Compose UI tests for important screen behavior.
- Use fakes from `core:testing` before introducing new mocking patterns.
- Current test libraries include JUnit, MockK, Turbine, coroutine test, Room testing, and Compose UI testing.

## Git And Generated Files

- Do not revert user changes unless explicitly asked.
- Avoid committing local machine artifacts.
- Keep Gradle wrapper files if already present.
- Use Conventional Commit messages, for example:
  - `feat: add profile editing flow`
  - `fix: preserve cached user on refresh failure`
  - `build: update compose compiler setup`
  - `test: add auth repository coverage`

## Documentation

Update `README.md` when changing module responsibilities, run commands, architecture, auth, local-first sync, or feature creation steps.
