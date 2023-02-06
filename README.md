# Vaadin Simple Security Example App

A demo project for the [Vaadin Simple Security](https://github.com/mvysny/vaadin-simple-security)
library.

# Preparing Environment

Please see the [Vaadin Boot](https://github.com/mvysny/vaadin-boot#preparing-environment) documentation
on how you run, develop and package this Vaadin-Boot-based app.

# About the application

The application uses the username+password authorization, with users stored in an in-memory H2 SQL database
(the [User](src/main/java/com/example/security/security/User.java) class;
uses [jdbi-orm](https://gitlab.com/mvysny/jdbi-orm) to map Java classes to SQL tables). There are no
views that could be accessed publicly - the user must log in first, in order to see any part of the app.

There are two users pre-created by the [Bootstrap](src/main/java/com/example/security/Bootstrap.java) class:

* The 'user' user with the password of 'user' and the role of `ROLE_USER`
* The 'admin' user with the password of 'admin' and two roles: `ROLE_ADMIN` and `ROLE_USER`

The [AppServiceInitListener](src/main/java/com/example/security/ApplicationServiceInitListener.java) configures
Vaadin to check authorization and redirects to the Login route if there's no user logged in.
The username and password are compared against the database. The `User` class takes advantage
of the `HasPassword`
mixin which makes sure to store the passwords in a hashed form.

If the login succeeds, the user is then stored into the session (or, rather, the `LoginService` class
is stored in the session along with the currently logged-in user. This way, we can group all
login/logout functionality into single class). Then, the page is refreshed. This forces Vaadin
to create a new instance of the `MainLayout`. Since a non-null user is now in the session, the `MainLayout`
will not perform the re-route to the login view; instead it will show the application layout.

There are four views:

* The [WelcomeRoute](src/main/java/com/example/security/welcome/WelcomeRoute.java) which is accessible by all logged-in users;
* The [UserRoute](src/main/java/com/example/security/user/UserRoute.java) which is accessible by all users with roles `ROLE_USER` and `ROLE_ADMIN`
* The [AdminRoute](src/main/java/com/example/security/admin/AdminRoute.java) which is accessible by users with the `ROLE_ADMIN` role only
