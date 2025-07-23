import com.github.spacemex.yml.YamlConfigTemplateWriter;
import com.github.spacemex.yml.YamlConfigUtil;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class GenerateTests {

    public static void main(String[] args) throws IOException {
        genYAMLTest();
    }

    private static void genYAMLTest(){
        System.out.println("Generating YAML file...");

        File yamlFile = new File("src/test/resources/exampleYamlConfig.yml");
        yamlFile.getParentFile().mkdirs();

        YamlConfigTemplateWriter writer = new YamlConfigTemplateWriter(yamlFile);

        writer.header("""
                This is A Header Comment
                Support Multi-line comments
                """)
                .add("test.string", "test", "This is a test string")
                .add("test.int", 123, "This is a test integer")
                .add("test.long", 9876543210L, "This is a test long")
                .add("test.boolean", true, "This is a test boolean")
                .add("test.float", 3.14f, "This is a test float")
                .add("test.double", 3.14159, "This is a test double")
                .add("test.list", Arrays.asList("a", "b", "c"), "This is a test list")
                .add("test.intList", Arrays.asList(1, 2, 3), "This is a test int list")
                .add("test.longList", Arrays.asList(10000000000L, 20000000000L), "This is a test long list")
                .add("test.doubleList", Arrays.asList(1.1, 2.2, 3.3), "This is a test double list")
                .add("test.mapString", Map.of("key1", "val1", "key2", "val2"), "This is a test string map")
                .add("test.mapInt", Map.of("key1", 10, "key2", 20), "This is a test int map")
                .add("test.mapDouble", Map.of("key1", 1.5, "key2", 2.5), "This is a test double map")
                .add("test.section.string", "nested", "Nested string")
                .add("test.section.int", 456, "Nested int")
                .add("test.section.boolean", false, "Nested boolean")
                .add("test.section.list.main", Arrays.asList("apple", "banana", "cherry"), "Main list of fruits")
                .add("test.section.list.secondary", Arrays.asList("orange", "grape"), "Secondary list of fruits")
                .add("test.section.list.tertiary", Arrays.asList("kiwi"), "Tertiary list")
                .write();

        System.out.println("Loading YAML file...");

        Yaml yaml = new Yaml();
        Map<String, Object> data;
        try (FileReader reader = new FileReader(yamlFile)) {
            data = yaml.load(reader);
        }catch (IOException e){
            throw new RuntimeException("Failed to load YAML config",e);
        }

        YamlConfigUtil util = new YamlConfigUtil(data);

        System.out.println("Testing basic getters:");
        System.out.println("test.string = " + util.getString("test.string", "default"));
        System.out.println("test.int = " + util.getInt("test.int", 0));
        System.out.println("test.long = " + util.getLong("test.long", 0));
        System.out.println("test.boolean = " + util.getBoolean("test.boolean", false));
        System.out.println("test.float = " + util.getFloat("test.float", 0));
        System.out.println("test.double = " + util.getDouble("test.double", 0));

        System.out.println("\nTesting list getters:");
        System.out.println("test.list = " + util.getStringList("test.list", Collections.emptyList()));
        System.out.println("test.intList = " + util.getIntList("test.intList", Collections.emptyList()));
        System.out.println("test.longList = " + util.getLongList("test.longList", Collections.emptyList()));
        System.out.println("test.doubleList = " + util.getDoubleList("test.doubleList", Collections.emptyList()));

        System.out.println("\nTesting map getters:");
        System.out.println("test.mapString = " + util.getMap("test.mapString", val -> {
            if (val instanceof String s) return s;
            return null;
        }, Collections.emptyMap()));

        System.out.println("test.mapInt = " + util.getMap("test.mapInt", val -> {
            if (val instanceof Number n) return n.intValue();
            if (val instanceof String s) return Integer.parseInt(s);
            return null;
        }, Collections.emptyMap()));

        System.out.println("test.mapDouble = " + util.getMap("test.mapDouble", val -> {
            if (val instanceof Number n) return n.doubleValue();
            if (val instanceof String s) return Double.parseDouble(s);
            return null;
        }, Collections.emptyMap()));

        System.out.println("\nTesting nested keys:");
        System.out.println("test.section.string = " + util.getString("test.section.string", "def"));
        System.out.println("test.section.int = " + util.getInt("test.section.int", 0));
        System.out.println("test.section.boolean = " + util.getBoolean("test.section.boolean", true));
        System.out.println("test.section.list = " + util.getStringList("test.section.list", Collections.emptyList()));

        System.out.println("\nTesting nested map using dot-path and generic getMap:");
        Map<String, Integer> nestedIntMap = util.getMap("test.section.nestedIntMap", val -> {
            if (val instanceof Number n) return n.intValue();
            if (val instanceof String s) return Integer.parseInt(s);
            return null;
        }, Map.of());
        System.out.println("test.section.nestedIntMap = " + nestedIntMap);

        System.out.println("\nTest complete.");

    }

}
