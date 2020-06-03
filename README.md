# MCDiscordReserializer ![Build](https://github.com/Vankka/MCDiscordReserializer/workflows/Build/badge.svg) ![Deploy](https://github.com/Vankka/MCDiscordReserializer/workflows/Deploy/badge.svg)
A library for transcoding between Minecraft and Discord.

Minecraft text is represented using [Kyori's text](https://github.com/KyoriPowered/text) (compatible with 3.x.x).

Discord text is represented using Java Strings (not relying on any specific Discord library) 
and is translated using a fork of [Discord's SimpleAST](https://github.com/discordapp/SimpleAST), 
[here](https://github.com/Vankka/SimpleAST).

## Dependency information

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
        <version>2.0.0</version>
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
    implementation 'dev.vankka:MCDiscordReserializer:2.0.0'
}
```

## Basic usageHow to use
```java
// For Minecraft -> Discord translating
String output = DiscordSerializer.INSTANCE.serialize(TextComponent.of("Bold").decoration(TextDecoration.BOLD, true));

// For Discord -> Minecraft translating
TextComponent output = MinecraftSerializer.INSTANCE.serialize("**Bold**");
```
