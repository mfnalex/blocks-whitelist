plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "com.jeff-media"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly(libs.spigot.api)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.wrapper {
    gradleVersion = "8.6"
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}

tasks.shadowJar {
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.register("copyToTestServer", Copy::class) {
    group  = "plugin"
    description = "Copies the plugin to the test server"
    from(tasks.shadowJar.get().archiveFile)
    into(getServerPluginsDirectory())
}