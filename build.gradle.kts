plugins {
    id("java")
    id("maven-publish")
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

// Disable bootJar as this is a library project without a main class
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

// Enable jar task for library projects
tasks.getByName<Jar>("jar") {
    enabled = true
}

group = "com.github.landsman"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    // Ensure the library is compatible with Java 23
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        // This allows the code to be run on Java 23 while being compiled with Java 17 compatibility
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate.orm:hibernate-core:7.0.0.Final")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.18.0")

    // Spring Data JPA dependencies for integration tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("org.postgresql:postgresql")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    testImplementation("org.flywaydb:flyway-core:9.22.3")

    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
    testImplementation("org.assertj:assertj-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
    failFast = true
    outputs.upToDateWhen { false }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("typeid-hibernate")
                description.set("Custom ID generator for Hibernate, Spring Framework inspired by @stripe")
                url.set("https://github.com/landsman/typeid-hibernate")
                version = project.version.toString()

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }

                developers {
                    developer {
                        id.set("landsman")
                        name.set("Michal Landsman")
                        email.set("landsmichal@gmail.com")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/landsman/typeid-hibernate")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
