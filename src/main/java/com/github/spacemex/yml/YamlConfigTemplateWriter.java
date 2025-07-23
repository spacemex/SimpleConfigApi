package com.github.spacemex.yml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 * Utility class for creating, merging, and writing YAML configuration files with support for nested keys,
 * comments, and header comments.
 * <p>
 * Allows adding keys with values and optional comments, automatically nesting keys by dot-separated paths,
 * merging with existing config files to preserve user changes and only add missing keys,
 * and writing the final YAML with comments and formatted values.
 */
public class YamlConfigTemplateWriter {

    private final File file;
    private final Map<String, String> comments = new HashMap<>();
    private final Map<String, Object> root = new LinkedHashMap<>();
    private final List<String> headerComments = new ArrayList<>();

    /**
     * Creates a new YamlConfigTemplateWriter for the specified file.
     *
     * @param file the YAML file to write to or merge with
     */
    public YamlConfigTemplateWriter(File file) {
        this.file = file;
    }
    /**
     * Adds a header comment block at the top of the YAML file.
     * Supports multi-line comments by splitting on newlines.
     *
     * @param comment the header comment string
     * @return this writer instance for chaining
     */
    public YamlConfigTemplateWriter header(String comment) {
        if (comment != null) {
            Collections.addAll(headerComments, comment.split("\n"));
        }
        return this;
    }
    /**
     * Adds a key-value pair to the configuration, with an optional comment.
     * Keys can be nested using dot notation, e.g. "section.subsection.key".
     *
     * @param path    the dot-separated key path
     * @param value   the value to assign to the key
     * @param comment an optional comment to include above the key
     * @return this writer instance for chaining
     */
    public YamlConfigTemplateWriter add(String path, Object value, String comment) {
        insertNested(root, path.split("\\."), value);
        comments.put(path, comment);
        return this;
    }

    /**
     * Recursively inserts a value into the nested map structure based on the given path.
     *
     * @param current the current map level
     * @param path    the split keys representing the nested path
     * @param value   the value to insert
     */
    @SuppressWarnings("unchecked")
    private void insertNested(Map<String, Object> current, String[] path, Object value) {
        for (int i = 0; i < path.length; i++) {
            String key = path[i];
            if (i == path.length - 1) {
                current.put(key, value);
            } else {
                Object nested = current.get(key);
                if (!(nested instanceof Map)) {
                    nested = new LinkedHashMap<String, Object>();
                    current.put(key, nested);
                }
                current = (Map<String, Object>) nested;
            }
        }
    }
    /**
     * Writes the YAML configuration to file.
     * If the file exists, loads its current contents, merges missing keys from the template,
     * and writes back preserving existing values and comments where possible.
     */
    @SuppressWarnings("unchecked")
    public void write() {
        Map<String, Object> existingData = new LinkedHashMap<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Object loaded = new org.yaml.snakeyaml.Yaml().load(reader);
                if (loaded instanceof Map<?, ?> map) {
                    existingData = (Map<String, Object>) map;
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read existing config", e);
            }
        }

        // Merge new keys from root into existingData (non-destructive)
        mergeMissing(existingData, root);

        try (FileWriter writer = new FileWriter(file)) {
            for (String line : headerComments) {
                writer.write("# " + line + System.lineSeparator());
            }

            if (!headerComments.isEmpty()) {
                writer.write(System.lineSeparator());
            }

            writeMap(writer, existingData, "", new LinkedList<>());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write merged config", e);
        }
    }

    /**
     * Recursively merges missing keys from source map into target map without overwriting existing keys.
     *
     * @param target the map to merge into (existing data)
     * @param source the map to merge from (new template data)
     */
    @SuppressWarnings("unchecked")
    private void mergeMissing(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!target.containsKey(key)) {
                target.put(key, value);
            } else if (value instanceof Map<?, ?> sourceMap && target.get(key) instanceof Map<?, ?> targetMap) {
                mergeMissing((Map<String, Object>) targetMap, (Map<String, Object>) sourceMap);
            }
        }
    }

    /**
     * Recursively writes a nested map to the FileWriter, including comments and proper indentation.
     *
     * @param writer    the FileWriter to write to
     * @param map       the map representing the current nested level
     * @param indent    the current indentation string (spaces)
     * @param pathParts the list of keys forming the full path (used for comments lookup)
     * @throws IOException if writing to the file fails
     */
    @SuppressWarnings("unchecked")
    private void writeMap(FileWriter writer, Map<String, Object> map, String indent, LinkedList<String> pathParts) throws IOException {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            pathParts.addLast(key);  // use addLast for clarity
            String fullPath = String.join(".", pathParts);

            String comment = comments.get(fullPath);
            if (comment != null && !comment.isBlank()) {
                writer.write(indent + "# " + comment + System.lineSeparator());
            }

            if (value instanceof Map<?, ?> nested) {
                writer.write(indent + key + ":" + System.lineSeparator());
                writeMap(writer, (Map<String, Object>) nested, indent + "  ", pathParts);
            } else {
                writer.write(indent + key + ": " + format(value) + System.lineSeparator());
            }

            pathParts.removeLast();
        }
    }


    /**
     * Formats an object value for YAML output.
     * Strings are quoted and escaped,
     * lists are formatted as inline arrays,
     * arrays are converted to lists,
     * other objects are converted to their string representation.
     *
     * @param value the value to format
     * @return the YAML-compatible string representation of the value
     */
    private String format(Object value) {
        if (value instanceof String s) {
            return "\"" + s.replace("\"", "\\\"") + "\"";
        } else if (value instanceof List<?> list) {
            return list.toString(); // simple format: [a, b, c]
        } else if (value != null && value.getClass().isArray()) {
            return Arrays.toString(convertArray(value));
        }
        return String.valueOf(value);
    }

    /**
     * Converts a primitive or object array into an Object[].
     *
     * @param array the array to convert
     * @return the converted Object[] array
     */
    private Object[] convertArray(Object array) {
        int length = java.lang.reflect.Array.getLength(array);
        Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            result[i] = java.lang.reflect.Array.get(array, i);
        }
        return result;
    }
}
