package com.eu.habbo.database;

import com.eu.habbo.core.ConfigurationManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class DatabasePool {
    private HikariDataSource database;

    public boolean getStoragePooling(ConfigurationManager config) {
        try {
            HikariConfig databaseConfiguration = new HikariConfig();
            databaseConfiguration.setMaximumPoolSize(config.getInt("db.pool.maxsize"));
            databaseConfiguration.setMinimumIdle(config.getInt("db.pool.minsize"));
            databaseConfiguration.setJdbcUrl("jdbc:mysql://" + config.getValue("db.hostname") + ":" + config.getValue("db.port") + "/" + config.getValue("db.database") + config.getValue("db.params"));
            databaseConfiguration.addDataSourceProperty("serverName", config.getValue("db.hostname"));
            databaseConfiguration.addDataSourceProperty("port", config.getValue("db.port"));
            databaseConfiguration.addDataSourceProperty("databaseName", config.getValue("db.database"));
            databaseConfiguration.addDataSourceProperty("user", config.getValue("db.username"));
            databaseConfiguration.addDataSourceProperty("password", config.getValue("db.password"));
            databaseConfiguration.addDataSourceProperty("dataSource.logger", "com.mysql.jdbc.log.StandardLogger");
            databaseConfiguration.addDataSourceProperty("dataSource.logSlowQueries", "true");
            databaseConfiguration.addDataSourceProperty("dataSource.dumpQueriesOnException", "true");
            databaseConfiguration.addDataSourceProperty("prepStmtCacheSize", "500");
            databaseConfiguration.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            databaseConfiguration.addDataSourceProperty("cachePrepStmts", "true");
            databaseConfiguration.addDataSourceProperty("useServerPrepStmts", "true");
            databaseConfiguration.addDataSourceProperty("rewriteBatchedStatements", "true");
            databaseConfiguration.addDataSourceProperty("useUnicode", "true");
            databaseConfiguration.setAutoCommit(true);
            databaseConfiguration.setConnectionTimeout(300000L);
            databaseConfiguration.setValidationTimeout(5000L);
            databaseConfiguration.setLeakDetectionThreshold(20000L);
            databaseConfiguration.setMaxLifetime(1800000L);
            databaseConfiguration.setIdleTimeout(600000L);
            this.database = new HikariDataSource(databaseConfiguration);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public HikariDataSource getDatabase() {
        return this.database;
    }
}