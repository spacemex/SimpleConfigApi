package com.github.spacemex;

/**
 * Interface defining basic configuration management operations.
 */
public interface ConfigManager {

    /**
     * Loads configuration data from the source (e.g., file, database).
     */
    void load();

    /**
     * Saves the current configuration data to the destination (e.g., file, database).
     */
    void save();
}