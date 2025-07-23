plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}



group = "com.github.spacemex"
version = "1.0.0"

java{
    toolchain{
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.4") // Yaml Support
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("SimpleConfigApi")
    archiveVersion.set(project.version.toString())
}

tasks.shadowJar {
    archiveBaseName.set("SimpleConfigApi")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")

    relocate("org.yaml.snakeyaml", "com.github.spacemex.shadow.snakeyaml")
}

tasks.withType<JavaCompile>{
    sourceCompatibility = "17"
    targetCompatibility = "17"
}


