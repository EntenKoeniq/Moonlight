package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ConfigurationManager {
    private final Properties properties;
    private final String configurationPath;
    public boolean loaded = false;
    public boolean isLoading = false;

    public ConfigurationManager(String configurationPath) {
        this.properties = new Properties();
        this.configurationPath = configurationPath;
        this.reload();
    }

    public void reload() {
        this.isLoading = true;
        this.properties.clear();

        InputStream input = null;

        try {
            File f = new File(this.configurationPath);
            input = Files.newInputStream(f.toPath());
            this.properties.load(input);
        } catch (IOException ex) {
            log.error("Failed to load config file.", ex);
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.loaded) {
            this.loadFromDatabase();
        }

        this.isLoading = false;
        log.info("Configuration Manager -> Loaded!");

        if (Emulator.getPluginManager() != null) {
            Emulator.getPluginManager().fireEvent(new EmulatorConfigUpdatedEvent());
        }
    }

    public void loadFromDatabase() {
        log.info("Loading configuration from database...");
        long millis = System.currentTimeMillis();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            if (statement.execute("SELECT * FROM emulator_settings")) {
                try (ResultSet set = statement.getResultSet()) {
                    while (set.next()) {
                        this.properties.put(set.getString("key"), set.getString("value"));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
        log.info("Configuration -> loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void saveToDatabase() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE emulator_settings SET `value` = ? WHERE `key` = ? LIMIT 1")) {
            for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey().toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public String getValue(String key) {
        return this.getValue(key, "");
    }

    public String getValue(String key, String defaultValue) {
        if (this.isLoading)
            return defaultValue;

        if (!this.properties.containsKey(key)) {
            log.error("Config key not found {}", key);
        }
        return this.properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (this.isLoading)
            return defaultValue;

        try {
            return (this.getValue(key, "0").equals("1")) || (this.getValue(key, "false").equals("true"));
        } catch (Exception e) {
            log.error("Failed to parse key {} with value '{}' to type boolean.", key, this.getValue(key));
        }
        return defaultValue;
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, Integer defaultValue) {
        if (this.isLoading)
            return defaultValue;

        try {
            return Integer.parseInt(this.getValue(key, defaultValue.toString()));
        } catch (Exception e) {
            log.error("Failed to parse key {} with value '{}' to type integer.", key, this.getValue(key));
        }
        return defaultValue;
    }

    public double getDouble(String key, Double defaultValue) {
        if (this.isLoading)
            return defaultValue;

        try {
            return Double.parseDouble(this.getValue(key, defaultValue.toString()));
        } catch (Exception e) {
            log.error("Failed to parse key {} with value '{}' to type double.", key, this.getValue(key));
        }

        return defaultValue;
    }

    public void update(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public void register(String key, String value) {
        if (this.properties.getProperty(key, null) != null)
            return;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO emulator_settings VALUES (?, ?)")) {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        this.update(key, value);
    }
}
