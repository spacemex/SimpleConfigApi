package com.github.spacemex.yml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Utility class for reading and extracting typed values from a nested YAML-like map structure.
 * Supports dot-separated key paths for nested access and provides getters for
 * primitive types, lists, and maps with default values.
 */
public class YamlConfigUtil {
    private final Map<String, Object> root;

    /**
     * Constructs a new YamlConfigUtil instance wrapping the given root map.
     *
     * @param root the root map representing the loaded YAML data
     */
    public YamlConfigUtil(Map<String, Object> root) {
        this.root = root;
    }

    /**
     * Retrieves a String value from the YAML structure by key path.
     *
     * @param key          dot-separated path to the value
     * @param defaultValue the default value to return if the key is missing or not a String
     * @return the String value or the defaultValue if not found or invalid type
     */
    public String getString(String key,String defaultValue){
        Object val = resolvePath(key);
        return val instanceof String ? (String)val : defaultValue;
    }

    /**
     * Retrieves an int value from the YAML structure by key path.
     * Supports parsing from String if necessary.
     *
     * @param key          dot-separated path to the value
     * @param defaultValue the default int value if missing or unparsable
     * @return the int value or defaultValue if not found or invalid
     */
    public int getInt(String key,int defaultValue){
        Object val = resolvePath(key);
        if (val instanceof Number) return ((Number)val).intValue();
        if (val instanceof String){
            try{
                return Integer.parseInt((String)val);
            }catch (NumberFormatException e){
                System.err.println("Failed to parse int value for key " +key);
                System.err.println("Returning default value: " +defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves a boolean value from the YAML structure by key path.
     * Supports parsing from String if necessary.
     *
     * @param key          dot-separated path to the value
     * @param defaultValue the default boolean value if missing or unparsable
     * @return the boolean value or defaultValue if not found or invalid
     */
    public boolean getBoolean(String key,boolean defaultValue){
        Object val = resolvePath(key);
        if (val instanceof Boolean) return (Boolean)val;
        if (val instanceof String) return Boolean.parseBoolean((String)val);
        return defaultValue;
    }

    /**
     * Retrieves a list of Strings from the YAML structure by key path.
     *
     * @param key          dot-separated path to the list
     * @param defaultValue the default list if missing or invalid type
     * @return the list of Strings or defaultValue if not found or invalid
     */
    public List<String> getStringList(String key,List<String> defaultValue){
        Object val = resolvePath(key);
        if (val instanceof List<?> list){
            return list.stream().filter(item -> item instanceof String)
                    .map(String.class::cast).toList();
        }
        return defaultValue;
    }

    /**
     * Retrieves a float value from the YAML structure by key path.
     * Supports parsing from String if necessary.
     *
     * @param key          dot-separated path to the value
     * @param defaultValue the default float value if missing or unparsable
     * @return the float value or defaultValue if not found or invalid
     */
    public float getFloat(String key, float defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof Number) return ((Number) val).floatValue();
        if (val instanceof String) {
            try {
                return Float.parseFloat((String) val);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse float value for key " + key);
                System.err.println("Returning default value: " + defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves a long value from the YAML structure by key path.
     * Supports parsing from String if necessary.
     *
     * @param key          dot-separated path to the value
     * @param defaultValue the default long value if missing or unparsable
     * @return the long value or defaultValue if not found or invalid
     */
    public long getLong(String key, long defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof Number) return ((Number) val).longValue();
        if (val instanceof String) {
            try {
                return Long.parseLong((String) val);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse long value for key " + key);
                System.err.println("Returning default value: " + defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves a list of Doubles from the YAML structure by key path.
     * Supports parsing list items from Strings if necessary.
     *
     * @param key          dot-separated path to the list
     * @param defaultValue the default list if missing or invalid
     * @return the list of Doubles or defaultValue if not found or invalid
     */
    public List<Double> getDoubleList(String key, List<Double> defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof List<?> list) {
            return list.stream().map(item -> {
                if (item instanceof Number) return ((Number) item).doubleValue();
                if (item instanceof String s) {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {}
                }
                return null;
            }).filter(Objects::nonNull).toList();
        }
        return defaultValue;
    }

    /**
     * Retrieves a double value from the YAML structure by key path.
     * Supports parsing from String if necessary.
     *
     * @param key          dot-separated path to the value
     * @param defaultValue the default double value if missing or unparsable
     * @return the double value or defaultValue if not found or invalid
     */
    public double getDouble(String key, double defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String) {
            try {
                return Double.parseDouble((String) val);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse double value for key " + key);
                System.err.println("Returning default value: " + defaultValue);
            }
        }
        return defaultValue;
    }


    /**
     * Retrieves a list of Integers from the YAML structure by key path.
     * Supports parsing list items from Strings if necessary.
     *
     * @param key          dot-separated path to the list
     * @param defaultValue the default list if missing or invalid
     * @return the list of Integers or defaultValue if not found or invalid
     */
    public List<Integer> getIntList(String key, List<Integer> defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof List<?> list) {
            return list.stream().map(item -> {
                if (item instanceof Number) return ((Number) item).intValue();
                if (item instanceof String s) {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {}
                }
                return null;
            }).filter(Objects::nonNull).toList();
        }
        return defaultValue;
    }

    /**
     * Retrieves a list of Longs from the YAML structure by key path.
     * Supports parsing list items from Strings if necessary.
     *
     * @param key          dot-separated path to the list
     * @param defaultValue the default list if missing or invalid
     * @return the list of Longs or defaultValue if not found or invalid
     */
    public List<Long> getLongList(String key, List<Long> defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof List<?> list) {
            return list.stream().map(item -> {
                if (item instanceof Number) return ((Number) item).longValue();
                if (item instanceof String s) {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException ignored) {}
                }
                return null;
            }).filter(Objects::nonNull).toList();
        }
        return defaultValue;
    }

    /**
     * Retrieves a map of String keys to String values from the YAML structure by key path.
     *
     * @param key          dot-separated path to the map
     * @param defaultValue the default map if missing or invalid
     * @return the String map or defaultValue if not found or invalid
     */
    public Map<String, String> getStringMap(String key, Map<String, String> defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .filter(e -> e.getKey() instanceof String && e.getValue() instanceof String)
                    .collect(Collectors.toMap(
                            e -> (String) e.getKey(),
                            e -> (String) e.getValue()
                    ));
        }
        return defaultValue;
    }

    /**
     * Retrieves a map of String keys to Integer values from the YAML structure by key path.
     * Supports parsing integer values from Strings if necessary.
     *
     * @param key          dot-separated path to the map
     * @param defaultValue the default map if missing or invalid
     * @return the Integer map or defaultValue if not found or invalid
     */
    public Map<String, Integer> getIntMap(String key, Map<String, Integer> defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof Map<?, ?> map) {
            Map<String, Integer> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String k) {
                    Object v = entry.getValue();
                    try {
                        if (v instanceof Number) result.put(k, ((Number) v).intValue());
                        else if (v instanceof String s) result.put(k, Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {}
                }
            }
            return result;
        }
        return defaultValue;
    }

    /**
     * Retrieves a map of String keys to Double values from the YAML structure by key path.
     * Supports parsing double values from Strings if necessary.
     *
     * @param key          dot-separated path to the map
     * @param defaultValue the default map if missing or invalid
     * @return the Double map or defaultValue if not found or invalid
     */
    public Map<String, Double> getDoubleMap(String key, Map<String, Double> defaultValue) {
        Object val = resolvePath(key);
        if (val instanceof Map<?, ?> map) {
            Map<String, Double> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String k) {
                    Object v = entry.getValue();
                    try {
                        if (v instanceof Number) result.put(k, ((Number) v).doubleValue());
                        else if (v instanceof String s) result.put(k, Double.parseDouble(s));
                    } catch (NumberFormatException ignored) {}
                }
            }
            return result;
        }
        return defaultValue;
    }

    /**
     * Retrieves a generic map from the YAML structure by key path.
     * The values are parsed using the provided {@link TParser}.
     *
     * @param <T>          the type of the map's values
     * @param path         dot-separated path to the map
     * @param parser       parser to convert map values to type T
     * @param defaultValue the default map if missing or invalid
     * @return the parsed map or defaultValue if not found or invalid
     */
    public <T> Map<String, T> getMap(String path, TParser<T> parser, Map<String, T> defaultValue) {
        Object val = resolvePath(path);
        if (val instanceof Map<?, ?> map) {
            Map<String, T> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String k) {
                    try {
                        T parsed = parser.parse(entry.getValue());
                        if (parsed != null) result.put(k, parsed);
                    } catch (Exception ignored) {}
                }
            }
            return result;
        }
        return defaultValue;
    }

    /**
     * Resolves a nested value inside the root map using a dot-separated key path.
     *
     * @param path the dot-separated key path (e.g. "section.sub.key")
     * @return the nested value or null if not found
     */
    private Object resolvePath(String path) {
        String[] keys = path.split("\\.");
        Object current = root;

        for (String key : keys) {
            if (!(current instanceof Map<?, ?> map)) return null;
            current = map.get(key);
        }

        return current;
    }

    /**
     * Returns a new {@link YamlConfigUtil} instance wrapping the nested map at the specified key path.
     *
     * @param key dot-separated path to a nested map section
     * @return a new YamlConfigUtil wrapping the nested map, or null if not found or not a map
     */
    @SuppressWarnings("unchecked")
    public YamlConfigUtil getSection(String key){
        Object val = resolvePath(key);
        if (val instanceof Map<?,?> map){
            return new YamlConfigUtil((Map<String, Object>) map);
        }
        return null;
    }
}
