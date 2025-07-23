package com.github.spacemex.yml;

import com.github.spacemex.ConfigManager;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlConfigManager implements ConfigManager {
    private final File configFile;
    private Map<String, Object> data;

    public YamlConfigManager(File file) {
        this.configFile = file;
    }

    @Override
    public void load() {
        try(InputStream input = new FileInputStream(configFile)){
            data = new Yaml().load(input);
        }catch (IOException e){
            throw new RuntimeException("Failed to load YAML config",e);
        }
    }

    @Override
    public void save() {
        try(Writer writer = new FileWriter(configFile)){
            new Yaml().dump(data, writer);
        }catch (IOException e){
            throw new RuntimeException("Failed to save YAML config",e);
        }
    }
}
