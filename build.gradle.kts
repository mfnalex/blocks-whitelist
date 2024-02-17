plugins {
    java
}

group = "com.jeff-media"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly(libs.spigot.api)
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        disableAutoTargetJvm()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.wrapper {
    gradleVersion = "8.6"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}

tasks.register("copyToTestServer", Copy::class) {
    group  = "plugin"
    description = "Copies the plugin to the test server"
    from(tasks.jar.get().archiveFile)
    into(getServerPluginsDirectory())
}