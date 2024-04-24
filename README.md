# typeid-hibernate

## Hibernate implementation of [TypeID](https://github.com/fxlae/typeid-java).

TypeIDs are a modern, type-safe, globally unique identifier based on the upcoming
UUIDv7 standard. They provide a ton of nice properties that make them a great choice
as the primary identifiers for your data in a database, APIs, and distributed systems.
Read more about TypeIDs in their [spec](https://github.com/jetpack-io/typeid).

## Installation

Starting with version `0.0.1`, `typeid-hibernate` requires at least Java 17.

For installation via Gradle:

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/landsman/typeid-hibernate")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.github.landsman:typeid-hibernate:0.0.1")
}
```

## Usage

```java
public class TestUserEntity {
    @Id
    @TypeIdHibernate(prefix = "u")
    private String id;
}
```

