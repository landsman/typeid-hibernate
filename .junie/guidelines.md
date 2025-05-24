# TypeID Hibernate Generator - Development Guidelines

## Build/Configuration Instructions

### Prerequisites
- Java 17 or higher
- Gradle (wrapper included in the project)

### Building the Project
The project uses Gradle as the build system. You can build the project using the included Gradle wrapper:

```bash
# Build the project
./gradlew build

# Generate Javadoc
./gradlew javadoc

# Create JAR files (main, sources, and javadoc)
./gradlew jar sourcesJar javadocJar
```

### Publishing to GitHub Packages
The project is configured to publish to GitHub Packages. To publish:

```bash
# Set GitHub credentials in gradle.properties or as environment variables
# GITHUB_ACTOR and GITHUB_TOKEN environment variables

# Publish to GitHub Packages
./gradlew publish
```

## Testing Information

### Running Tests
Tests use JUnit 5 with Mockito for mocking. To run tests:

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.github.landsman.TypeIdHibernateGeneratorTest"
```

### Adding New Tests
1. Create a test class in the `src/test/java/com/github/landsman` directory
2. Use the `@ExtendWith(MockitoExtension.class)` annotation for Mockito support
3. Mock the `SharedSessionContractImplementor` for Hibernate session
4. Create test entities with the `@TypeIdHibernate` annotation

### Example Test
Here's a simple test example:

```java
package com.github.landsman;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomEntityTest {

    @Mock
    private SharedSessionContractImplementor session;

    private TypeIdHibernateGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new TypeIdHibernateGenerator();
    }

    @Test
    public void testCustomEntityIdGeneration() {
        // Create a test entity
        CustomEntity entity = new CustomEntity();
        
        // Generate an ID
        String generatedId = (String) generator.generate(session, entity);
        
        // Verify the ID starts with the expected prefix
        assertTrue(generatedId.startsWith("custom"), 
                   "The generated ID should start with the prefix 'custom'");
    }
}

// Example test entity
class CustomEntity {
    @TypeIdHibernate(prefix = "custom")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
```

## Additional Development Information

### Code Style
- Follow standard Java code conventions
- Use meaningful variable and method names
- Add Javadoc comments for public classes and methods

### Library Usage in Applications
To use this library in your application:

1. Add the dependency to your project:
   ```gradle
   implementation("com.github.landsman:typeid-hibernate:0.0.1")
   ```

2. Add the `@TypeIdHibernate` annotation to your entity ID fields:
   ```java
   @Entity
   public class User {
       @Id
       @TypeIdHibernate(prefix = "user")
       private String id;
       
       // Other fields and methods
   }
   ```

3. The ID will be automatically generated when the entity is persisted.

### Troubleshooting
- If you encounter Mockito warnings about "self-attaching to enable the inline-mock-maker", consider adding Mockito as a Java agent in your build configuration as described in Mockito's documentation.
- For issues with TypeID generation, verify that the entity class has a field annotated with `@TypeIdHibernate` and that the prefix is specified.