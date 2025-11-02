import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("dev.nx.gradle.project-graph") version("0.1.8")
    id("org.springframework.boot") version "3.5.6" apply false
}

allprojects {
    apply(plugin = "dev.nx.gradle.project-graph")
    repositories {
        mavenLocal()
        maven {
            url = uri("https://repo.maven.apache.org/maven2/")
        }
    }
}

subprojects {
    group = "com.decathlon.gifting"
    version = "6.0.0-SNAPSHOT"

    apply(plugin = "io.spring.dependency-management")
    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom(BOM_COORDINATES)
            // version should be compatible with Spring Boot: see https://spring.io/projects/spring-cloud
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
        }
        generatedPomCustomization {
            dependencies {
                var mapstructVersion = "1.6.3"
                dependency("org.mapstruct:mapstruct:$mapstructVersion")
                dependency("org.mapstruct:mapstruct-processor:$mapstructVersion")
                dependency("org.projectlombok:lombok-mapstruct-binding:0.2.0")
            }
        }
    }

    // Believe it or not, this project contains Java code
    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType(Test::class) {
        // Register the JUnit 5
        useJUnitPlatform()
        // Fine tune how tests logs in the console
        testLogging {
            displayGranularity = 2
            events(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR)
            showExceptions = true
            showCauses = true
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(
            arrayOf(
                "-parameters"
            )
        )
        options.encoding = "UTF-8"
    }

    dependencies {
        add("compileOnly", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok")
        add("annotationProcessor", "org.projectlombok:lombok-mapstruct-binding")
        add("annotationProcessor", "org.mapstruct:mapstruct-processor")

        add("implementation", "org.mapstruct:mapstruct")
        add("implementation", "org.apache.commons:commons-lang3")

        add("testCompileOnly", "org.projectlombok:lombok")
        add("testAnnotationProcessor", "org.projectlombok:lombok")
        add("testAnnotationProcessor", "org.projectlombok:lombok-mapstruct-binding")
        add("testAnnotationProcessor", "org.mapstruct:mapstruct-processor")

        add("testImplementation", "org.mockito:mockito-core")
        add("testImplementation", "org.junit.jupiter:junit-jupiter-engine")
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
        add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }
}

/* Jacoco Report */
val jacocoReport by tasks.registering(JacocoReport::class) {
    group = "Coverage reports"
    description = "Generates an aggregate report from all subprojects"
}

allprojects {
    apply(plugin = "jacoco")
    apply(plugin = "project-report")

    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.13"
    }

    plugins.withType<JacocoPlugin> {
        val testTasks = tasks.withType<Test>()

        jacocoReport {
            // Some of the projects might fail to create a file (e.g. no tests or no coverage),
            // So we check for file existence. Otherwise JacocoReport would fail
            val execFiles = files(testTasks).filter { it.exists() && it.name.endsWith(".exec") }
            executionData(execFiles)
        }

        tasks.withType<JacocoReport>().configureEach {
            reports {
                xml.required.set(true) // Sonarcloud prefers xml reports
            }
        }
        // Add each project to combined report
        plugins.withId("java") {
            configure<SourceSetContainer> {
                val mainCode = getByName("main")
                jacocoReport {
                    additionalSourceDirs.from(mainCode.allJava.srcDirs)
                    sourceDirectories.from(mainCode.allSource.srcDirs)
                    classDirectories.from(mainCode.output.asFileTree.matching {
                        exclude("module-info.class")
                    })
                }
            }
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
