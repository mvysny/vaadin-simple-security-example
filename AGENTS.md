# Vaadin Simple Security Example App — AGENTS.md

## What this is

A demo project for the [Vaadin Simple Security](https://github.com/mvysny/vaadin-simple-security)
library: username+password login and role-based route access for a Vaadin Flow app that runs
from a plain `main()` on [Vaadin Boot](https://github.com/mvysny/vaadin-boot), no Spring.
The library owns the access check and the session; this app owns the users table, the login
screen and the routes it guards. Requires Java 21+.

## Promises

- **No Spring.** A plain `main()` on Vaadin Boot; the demo exists to show the library working without a DI container.
- **Minimal.** Only what shows the library's login, roles and route guarding; anything else is cut.

## Design docs

| File | Owns | Loaded |
|---|---|---|
| `README.md` | the pitch, how to run, the demo users | — |
| `AGENTS.md` (this) | promises, invariants, the module map, conventions, commands | every turn |
| `design/architecture.md` | how the pieces compose — the wiring that replaces a DI container, the login and logout flows; normative | lazy |
| doc comments | what one symbol does and why it is shaped so | at the symbol |

Every fact lives in exactly one of these; the others link to it.

## Invariants

- **Every route and every router layout (`MainLayout`) carries exactly one of `@AnonymousAllowed`, `@PermitAll`, `@RolesAllowed`.** Without one, the access control denies it to everyone.
- **Every `VaadinServiceInitListener` is listed in `src/main/resources/META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener`.** Unlisted, it silently never runs; the wiring is in architecture.md.
- **The logged-in user lives in the session, reached through `LoginService.get()`.** A user held in a static or a singleton leaks across sessions.

## Module map

- `com.example.security` — the entry point `Main`, startup wiring, the app shell and `MainLayout`, the authenticated shell the content routes sit in.
- `com.example.security.security` — login: the login route, the session-scoped `LoginService`, the `User` entity and its DAO.
- `com.example.security.{welcome,user,admin}` — one route each, by access class: any logged-in user, `ROLE_USER`, `ROLE_ADMIN`.
- `src/main/resources/db/migration` — Flyway migrations, `V<nn>__<Name>.sql`.
- `src/test/java` — Karibu tests mirroring the main packages, all on `AbstractAppTester`.

## Conventions

- **Java 21, Vaadin Flow on Vaadin Boot.** Embedded Jetty from `Main.main()`; no DI container.
- **Persistence: in-memory H2 behind Hikari, Flyway migrations, JDBI-ORM entities** with a static `dao`; components query it directly.
- **A view is a `*Route` in a package of its own**, with `layout = MainLayout.class` when it sits in the authenticated shell.
- **Dependency versions live in `gradle/libs.versions.toml`**, never inline in `build.gradle.kts`.
- **Tests: Karibu-Testing, no browser.** Extend `AbstractAppTester`, assert with `LocatorJ` (`_assertOne`, `_assertNone`, `_get`); preferred over integration tests.
- **Logging: SLF4J to slf4j-simple**, levels in `src/main/resources/simplelogger.properties`.
- **A demo user's password equals its username** — `user`/`user` (`ROLE_USER`), `admin`/`admin` (`ROLE_ADMIN,ROLE_USER`), seeded by `Bootstrap`; the tests' `login(username)` relies on it.

## Commands

```bash
./gradlew build                              # clean+build (the configured default tasks); `check` also runs design/verify_design_tripwires.sh
./gradlew run                                # start the app on http://localhost:8080
./gradlew test                               # run all tests
./gradlew test --tests com.example.security.security.LoginServiceTest      # single test class
./gradlew test --tests com.example.security.security.LoginServiceTest.successfulLogin  # single test method
./gradlew build -Pvaadin.productionMode      # production build (bundles frontend; produces build/distributions/app.tar)
./gradlew vaadinClean                        # nuke node_modules / generated frontend if Vite/pnpm gets wedged
design/verify_design_tripwires.sh            # the doc-layer checks alone
```

CI (`.github/workflows/gradle.yml`, on push and PR): `./gradlew clean build -Pvaadin.productionMode` on JDK 21 and 25, and the tripwire script in a job of its own.

`Dockerfile` does a multi-stage production build; its header comments have the `docker build` / `docker run` incantations.

## Skills this project follows

- **Component-oriented:** self-sufficient components that reach the DB directly, no MVC layers; the `cop` skill has the rules.
- **Karibu-Testing:** browserless lookups, `_click` / `_setValue`, the `karibu-testing-*` artifact matching the Vaadin version; the `karibu-testing` skill.
- **Vaadin Boot app:** views, styles, tests and persistence in a no-Spring Vaadin Boot app; the `vaadin-boot-app` skill.

## Maintenance of this file

Loaded every turn; cap 34 KB. Over it, in this order:
delete what has no home — status, history, class lists, what the code already says; trim
each line to its fact plus one clause and send the explanation home — why →
`design/decisions.md`, how across symbols → `design/architecture.md`, how in one symbol →
its doc comment, what upstream does → `design/research.md`; only then a module's own
`AGENTS.md`, peripheral modules first, never the core. Never paraphrase a lazy entry into a
line here. `design/verify_design_tripwires.sh` checks the cap and the cites.
