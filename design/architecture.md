# Architecture

How the pieces compose — what no single symbol can say and what would be expensive to overturn:
the wiring that stands in for a DI container, the login and logout flows, where to start reading.
**Normative: the code conforms.** Change this file first, then the code. Not here: one symbol's
behaviour (its doc comment), the module map (`AGENTS.md`).

## Wiring

- No DI container. The servlet container and Vaadin discover the two startup hooks; `Main.main()` only calls `new VaadinBoot().run()`, and Vaadin Boot starts Jetty, which finds both.
- `Bootstrap` is a `@WebListener`: on startup it points JDBI-ORM at a Hikari pool over in-memory H2, runs the Flyway migrations from `src/main/resources/db/migration`, and seeds the two demo users.
- `ApplicationServiceInitListener` is registered in `src/main/resources/META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener`. It builds one `SimpleNavigationAccessControl` bound to `LoginService::get`, with `LoginRoute` as the login view, and adds it as a `BeforeEnterListener` to every UI.
- Authorization is declarative: on every navigation the access control reads the `jakarta.annotation.security` annotation of the target route and of its router layout, and reroutes to `LoginRoute` when there is no user.
- Components reach the data directly: `LoginService` and `AdminRoute`'s grid query `User` through JDBI-ORM, with no service layer between.
- The test seam: `AbstractAppTester` calls `Bootstrap.contextInitialized` itself — no servlet container — once per test class, and `MockVaadin.setup` per test with the routes auto-discovered from `com.example.security`. The real routes, layout and access control then run against a mocked UI.

## Flows

**Logging in:**

1. A browser with no user in its session navigates anywhere; the access control finds no user in `LoginService.get()` and reroutes to `LoginRoute`.
2. `LoginRoute`'s form calls `LoginService.get().login(username, password)`, which looks the user up with `User.dao.findByUsername` and checks `passwordMatches`; a miss throws `FailedLoginException`, and the form shows its error.
3. On a match, `AbstractLoginService.login(user)` stores the user in the session and moves the browser on into the app.
4. The navigation builds a fresh `MainLayout`; the access control now finds a user whose roles the target route allows, and the route renders inside the layout.

**Logging out:** `MainLayout`'s button calls `LoginService.get().logout()`, which closes the Vaadin session, invalidates the HTTP session and reloads the page; the reload has no user, so step 1 above applies.

## Where to start reading

`ApplicationServiceInitListener` — a dozen lines that connect the library's access control to this app's `LoginService` and `LoginRoute`; then `LoginService` for the session-scoped user every check reads.
