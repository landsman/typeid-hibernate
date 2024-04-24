plugins {
    id("java")
}

group = "com.github.landsman"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("de.fxlae:typeid-java:0.3.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.0.0")
}

tasks.test {
    useJUnitPlatform()
}