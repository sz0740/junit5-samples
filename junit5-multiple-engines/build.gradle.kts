plugins {
    java
    groovy
    eclipse // optional (to generate Eclipse project files)
    idea // optional (to generate IntelliJ IDEA project files)
    kotlin("jvm") version "1.3.50"
}

repositories {
    mavenCentral()
    jcenter {
        content {
            includeGroup("org.spekframework.spek2")
            includeGroup("spek")
        }
    }
}

dependencies {
    val junit4Version = "4.13.2"
    val junitBomVersion = "5.7.2"

    // Use junit-bom to align versions
    // https://docs.gradle.org/current/userguide/managing_transitive_dependencies.html#sec:bom_import
    implementation(platform("org.junit:junit-bom:$junitBomVersion")) {
        because("Platform, Jupiter, and Vintage versions should match")
    }

    // JUnit Jupiter
    testImplementation("org.junit.jupiter:junit-jupiter")

    // JUnit Vintage
    testImplementation("junit:junit:$junit4Version")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine") {
        because("allows JUnit 3 and JUnit 4 tests to run")
    }

    // JUnit Platform Launcher + Console
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("allows tests to run from IDEs that bundle older version of launcher")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-console") {
        because("needed to launch the JUnit Platform Console program")
    }

    // Mainrunner
    testImplementation("de.sormuras.mainrunner:de.sormuras.mainrunner.engine:2.1.4") {
        because("executes Java programs as tests")
    }

    // jqwik
    testImplementation("net.jqwik:jqwik:1.5.1") {
        because("allows jqwik properties to run")
    }

    // Spek2
    val spekVersion = "2.0.15"
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")

    // Spock2
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0") {
        because("allows Spock specifications to run")
    }

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:4.5.0")
    testRuntimeOnly("org.slf4j:slf4j-nop:1.7.30") {
        because("defaulting to no-operation (NOP) logger implementation")
    }

    // TestNG
    testImplementation("org.testng:testng:7.4.0") {
        because("allows TestNG tests to run")
    }
    testRuntimeOnly("com.github.testng-team:testng-junit5:0.0.1") {
        because("allows TestNG tests to run")
    }
}

tasks {

    val consoleLauncherTest by registering(JavaExec::class) {
        dependsOn(testClasses)
        val reportsDir = file("$buildDir/test-results")
        outputs.dir(reportsDir)
        classpath = sourceSets.test.get().runtimeClasspath
        main = "org.junit.platform.console.ConsoleLauncher"
        args("--scan-classpath")
        args("--include-classname", ".*((Tests?)|(Spec))$")
        args("--details", "tree")
        args("--reports-dir", reportsDir)
    }

    test {
        // useJUnitPlatform() ... https://github.com/gradle/gradle/issues/4912
        dependsOn(consoleLauncherTest)
        exclude("**/*")
    }
}
