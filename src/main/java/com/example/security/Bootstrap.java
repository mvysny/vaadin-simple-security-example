package com.example.security;

import com.example.security.security.User;
import com.gitlab.mvysny.jdbiorm.JdbiOrm;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.h2.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class Bootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Starting up");

        // this will configure your database. For demo purposes, an in-memory embedded H2 database is used. To use a production-ready database:
        // 1. fill in the proper JDBC URL here
        // 2. make sure to include the database driver into the classpath, by adding a dependency on the driver into the build.gradle file.
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName(Driver.class.getName());
        cfg.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        cfg.setUsername("sa");
        cfg.setPassword("");
        JdbiOrm.setDataSource(new HikariDataSource(cfg));

        // Makes sure the database is up-to-date. See src/main/resources/db/migration for db init scripts.
        log.info("Running DB migrations");
        Flyway flyway = Flyway.configure()
                .dataSource(JdbiOrm.getDataSource())
                .load();
        flyway.migrate();

        // setup security
        final User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoles("ROLE_ADMIN,ROLE_USER");
        admin.save();
        final User user = new User();
        user.setUsername("user");
        user.setPassword("user");
        user.setRoles("ROLE_USER");
        user.save();

        log.info("Initialization complete");
    }

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
}
