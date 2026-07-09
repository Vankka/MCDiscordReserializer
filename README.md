# MCDiscordReserializer 

[![Maven Central](https://img.shields.io/maven-central/v/dev.vankka/mcdiscordreserializer?label=release)](https://central.sonatype.com/search?q=g%3Adev.vankka+a%3Amcdiscordreserializer)
![Sonatype (Snapshots)](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fvankka%2Fmcdiscordreserializer%2Fmaven-metadata.xml&label=snapshot)

A library for transcoding between Minecraft and Discord.

Minecraft text is represented using [Kyori's Adventure](https://github.com/KyoriPowered/adventure).

Discord text is represented using Java Strings (not relying on any specific Discord library) 
and is translated using a fork of [Discord's SimpleAST](https://github.com/discordapp/SimpleAST), 
[here](https://github.com/Vankka/SimpleAST).

## Dependency information

### Version
| Adventure Version | MCDiscordReserializer version | Maintained |
|-------------------|-------------------------------|------------|
| 5.x.x             | 5.x.x                         | ✔          |
| 4.x.x             | 4.x.x                         | ✖️         |

#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>dev.vankka</groupId>
        <artifactId>mcdiscordreserializer</artifactId>
        <version>4.3.0</version>
    </dependency>
</dependencies>
```

### Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'dev.vankka:mcdiscordreserializer:4.3.0'
}
```


## Basic usage
```java
// For Minecraft -> Discord translating
String output = DiscordSerializer.INSTANCE.serialize(TextComponent.of("Bold").decoration(TextDecoration.BOLD, true));

// For Discord -> Minecraft translating
TextComponent output = MinecraftSerializer.INSTANCE.serialize("**Bold**");
```
