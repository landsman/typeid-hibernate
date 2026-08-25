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

### Recommended Usage

Use the `@IdTypeId` annotation for a clean, simplified approach:

```java
public class TestUserEntity {
    @IdTypeId(prefix = "u")
    private String id;
}
```

The `@IdTypeId` annotation combines the functionality of:
- `@Id` (JPA primary key)
- `@Column(unique = true)` (database-level uniqueness)
- `@Type(OptimizedTypeIdType.class)` (Hibernate type)
- TypeID generation functionality

This allows you to use a single annotation instead of four separate ones, making your code cleaner and more maintainable.

### Legacy Usage (Deprecated)

The following approach is deprecated and should be avoided in new code:

```java
public class TestUserEntity {
    @Id
    @Column(unique = true) // This ensures database-level uniqueness
    @Type(OptimizedTypeIdType.class)
    @TypeIdHibernate(prefix = "u") // Deprecated
    private String id;
}
```

The `@TypeIdHibernate` annotation has been deprecated in favor of `@IdTypeId`, which provides the same functionality with a single annotation.
