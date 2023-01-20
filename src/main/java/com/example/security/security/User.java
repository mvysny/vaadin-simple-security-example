package com.example.security.security;

import com.github.mvysny.vaadinsimplesecurity.HasPassword;
import com.gitlab.mvysny.jdbiorm.Dao;
import com.gitlab.mvysny.jdbiorm.Entity;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a user. Stored in a database; see {@link Entity} and <a href="https://gitlab.com/mvysny/jdbi-orm">JDBI-ORM</a> for more details.
 * Implements the {@link HasPassword} helper interface which provides password hashing functionality. Remember to set the
 * password via {@link #setPassword(String)} and verify the password via {@link #passwordMatches(String)}.
 */
public final class User implements Entity<Long>, HasPassword {
    private Long id;
    /**
     * user name, unique
     */
    @NotNull
    private String username;

    /**
     * The hashed password.
     */
    @NotNull
    private String hashedPassword;

    /**
     * Comma-separated list of roles
     */
    @NotNull
    private String roles;

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @org.jetbrains.annotations.NotNull
    public Set<String> getRoleSet() {
        final String r = getRoles();
        return r == null ? Set.of() : Set.of(r.split(","));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(hashedPassword, user.hashedPassword) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, hashedPassword, roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }

    public static class UserDao extends Dao<User, Long> {
        public UserDao() {
            super(User.class);
        }

        /**
         * Finds user by his username. If there is no such user, returns `null`.
         * @param username the user name
         * @return the user or null.
         */
        @Nullable
        public User findByUsername(@org.jetbrains.annotations.NotNull String username) {
            return findSingleBy("username = :username", q -> q.bind("username", username));
        }
    }

    public static final UserDao dao = new UserDao();
}
