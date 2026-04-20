# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Demo app for the [Vaadin Simple Security](https://github.com/mvysny/vaadin-simple-security) library. Vaadin Flow UI on top of [Vaadin Boot](https://github.com/mvysny/vaadin-boot) (embedded Jetty, plain `main()`, **no Spring**). Java 21+, Gradle Kotlin DSL, in-memory H2 + Flyway + JDBI-ORM.

## Common commands

```bash
./gradlew build                              # clean+build (the configured default tasks)
./gradlew run                                # start the app on http://localhost:8080
./gradlew test                               # run all tests
./gradlew test --tests com.example.security.security.LoginServiceTest      # single test class
./gradlew test --tests com.example.security.security.LoginServiceTest.successfulLogin  # single test method
./gradlew build -Pvaadin.productionMode      # production build (bundles frontend; produces build/distributions/app.tar)
./gradlew vaadinClean                        # nuke node_modules / generated frontend if Vite/pnpm gets wedged
```

Dockerfile does a multi-stage production build; see its header comments for the `docker build` / `docker run` incantations.

Pre-seeded credentials (see `Bootstrap`): `user`/`user` (`ROLE_USER`) and `admin`/`admin` (`ROLE_ADMIN,ROLE_USER`).

Dependency versions live in `gradle/libs.versions.toml`. Bumping Vaadin, Vaadin Boot, Flyway, etc. is done there, not in `build.gradle.kts`.

## Architecture

### Wiring (there is no DI container)

Two discovery mechanisms replace Spring:

- **`Bootstrap`** — annotated `@WebListener`, so the servlet container runs it on startup. Configures Hikari → H2, runs Flyway (`src/main/resources/db/migration/V*__*.sql`), and seeds the two demo users.
- **`ApplicationServiceInitListener`** — registered via `src/main/resources/META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener`. It constructs a `SimpleNavigationAccessControl` bound to `LoginService::get` and attaches it as a `BeforeEnterListener` on every UI. **Any new `VaadinServiceInitListener` must be added to that `META-INF/services` file** or it will silently not run.

`Main.main()` just calls `new VaadinBoot().run()` — Vaadin Boot discovers the `@WebListener` and the `META-INF/services` registration itself.

### Authorization model

Authorization is enforced by `SimpleNavigationAccessControl` reading standard `jakarta.annotation.security` annotations on routes. Every route (and `MainLayout`, which wraps the content routes) **must** carry exactly one of:

- `@AnonymousAllowed` — e.g. `LoginRoute`
- `@PermitAll` — any logged-in user; `MainLayout` and `WelcomeRoute` use this
- `@RolesAllowed({...})` — e.g. `UserRoute` requires `ROLE_USER`/`ROLE_ADMIN`, `AdminRoute` requires `ROLE_ADMIN`

A route with no annotation is effectively inaccessible. When adding a new route, decide the access class and annotate it; `layout = MainLayout.class` is the common pattern for anything inside the authenticated shell.

### Session / login lifecycle

`LoginService extends AbstractLoginService<User>` and is instantiated per-session via the `AbstractLoginService.get(Class, Supplier)` helper — call `LoginService.get()` anywhere to get the session-scoped instance. On successful `login(username, password)` it stores the user in the session and triggers a page refresh; `MainLayout` then sees a non-null user and renders instead of redirecting to `LoginRoute`. `logout()` invalidates the session.

`User` is a JDBI-ORM `Entity<Long>` mapped to the `users` table, with a static `User.dao` (a `Dao<User, Long>` subclass) for queries like `findByUsername`. The `HasPassword` mixin handles BCrypt-style hashing — use `setPassword(plain)` and `passwordMatches(plain)`, never touch `hashedPassword` directly except when wiring up mapping.

### Testing (Karibu-Testing, no browser)

Tests extend `AbstractAppTester`, which:

- Runs `Bootstrap` once in `@BeforeAll` to get the DB + demo users, tears it down in `@AfterAll`.
- `MockVaadin.setup(routes)` / `tearDown()` per test, with routes auto-discovered from `com.example.security`.
- `login(username)` helper uses the demo convention that password equals username.

Assertions use `LocatorJ._assertOne(...)`, `_assertNone(...)`, `_get(...)` etc. — these run the real route/layout code against a mocked UI; no Selenium or Jetty. This is fast; prefer it over integration tests when adding coverage.
