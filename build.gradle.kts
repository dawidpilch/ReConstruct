plugins {
    id("java")
    id("application")
    id("net.researchgate.release") version "3.1.0"
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
    id("org.gradlex.extra-java-module-info") version "1.0"
}

application {
    mainClass = "com.reconstruct.Main"
}

extraJavaModuleInfo {
    module("commons-math3-3.6.1.jar", "commons.math3", "3.6.1") {
        exports("org.apache.commons.math3.util")
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    mavenCentral()
}

javafx {
    version = "23"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.graphics")
}

dependencies {
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude("org.openjfx")
    }

    implementation("org.apache.commons:commons-math3:3.6.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

jlink {
    imageZip = project.file("${layout.buildDirectory}/distributions/app-${javafx.platform.classifier}.zip")
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "app"
    }
}

tasks.distTar {
    enabled = false
}