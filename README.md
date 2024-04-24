# typeid-hibernate

## Hibernate implementation of [TypeID](https://github.com/fxlae/typeid-java).

TypeIDs are a modern, type-safe, globally unique identifier based on the upcoming
UUIDv7 standard. They provide a ton of nice properties that make them a great choice
as the primary identifiers for your data in a database, APIs, and distributed systems.
Read more about TypeIDs in their [spec](https://github.com/jetpack-io/typeid).

## Installation

Starting with version `0.0.1`, `typeid-hibernate` requires at least Java 17.

To install via Maven:

```xml
<dependency>
    <groupId>com.github.landsman</groupId>
    <artifactId>typeid-hibernate</artifactId>
    <version>0.0.1</version>
</dependency>
```

For installation via Gradle:

```kotlin
implementation("com.github.landsman:typeid-hibernate:0.0.1")
```

## Usage

```java
public class TestUserEntity {
    @Id
    @TypeIdHibernate(prefix = "u")
    private String id;
}
```

