# MCDiscordReserializer ![Build](https://github.com/Vankka/MCDiscordReserializer/workflows/Build/badge.svg) ![Deploy](https://github.com/Vankka/MCDiscordReserializer/workflows/Deploy/badge.svg)
A library for transcoding between Minecraft and Discord.

Minecraft text is represented using [Kyori's Adventure](https://github.com/KyoriPowered/adventure) (compatible with 4.x.x).

Discord text is represented using Java Strings (not relying on any specific Discord library) 
and is translated using a fork of [Discord's SimpleAST](https://github.com/discordapp/SimpleAST), 
[here](https://github.com/Vankka/SimpleAST).

## Dependency information

### Version
| Text/Adventure Version | MCDiscordReserializer version | Maintained |
|----|----|----|
| 4.x.x | 4.x.x | ✔ |
| 3.x.x | 3.x.x | ️ |

### Versions 4.2.0 and up

#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>dev.vankka</groupId>
        <artifactId>MCDiscordReserializer</artifactId>
        <version>4.2.0</version>
    </dependency>
</dependencies>
```

### Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'dev.vankka:MCDiscordReserializer:4.2.0'
}
```

<details>
    <summary>Versions Before 4.2.0</summary>

    #### Maven
    ```xml
    <repositories>
        <repository>
            <id>Vankka-Nexus</id>
            <url>https://nexus.vankka.dev/repository/maven-public/</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>dev.vankka</groupId>
            <artifactId>MCDiscordReserializer</artifactId>
            <version>3.0.1</version>
        </dependency>
    </dependencies>
    ```
    
    #### Gradle
    ```groovy
    repositories {
        maven { 
          id 'Vankka-Nexus'
          url 'https://nexus.vankka.dev/repository/maven-public/' 
        }
    }
    
    dependencies {
        implementation 'dev.vankka:MCDiscordReserializer:3.0.1'
    }
    ```
</details>


## Basic usage
```java
// For Minecraft -> Discord translating
String output = DiscordSerializer.INSTANCE.serialize(TextComponent.of("Bold").decoration(TextDecoration.BOLD, true));

// For Discord -> Minecraft translating
TextComponent output = MinecraftSerializer.INSTANCE.serialize("**Bold**");
```
