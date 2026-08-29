package com.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("application.properties файл олдсонгүй!");
                throw new RuntimeException("application.properties file not found in resources");
            }
            props.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            // Pool-ийн тохиргоонууд
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.max-size", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.min-idle", "2")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idle-timeout", "300000")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connection-timeout", "20000")));

            // Гүйцэтгэл сайжруулах тохиргоонууд
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP Connection Pool амжилттай үүслээ.");

        } catch (Exception e) {
            logger.error("Өгөгдлийн сангийн холболтыг эхлүүлэхэд алдаа гарлаа", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}