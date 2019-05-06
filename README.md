# MCDiscordReserializer [![Build Status](https://travis-ci.org/Vankka/MCDiscordReserializer.svg?branch=master)](https://travis-ci.org/Vankka/MCDiscordReserializer)
A library for transcoding between Minecraft and Discord.

Minecraft text is represented using [Kyori's text](https://github.com/KyoriPowered/text).

Discord text is represented using Java Strings (not relying on any specific Discord library) 
and is translated using a fork of [Discord's SimpleAST](https://github.com/discordapp/SimpleAST), 
[here](https://github.com/Vankka/SimpleAST).

## Dependency information

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Vankka</groupId>
    <artifactId>MCDiscordReserializer</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

#### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Vankka:MCDiscordReserializer:master-SNAPSHOT'
}
```

## How to use
```java
// For Minecraft -> Discord translating
String output = DiscordSerializer.serialize(TextComponent.of("Bold").decoration(TextDecoration.BOLD, true));

// For Discord -> Minecraft translating
TextComponent output = MinecraftSerializer.serialize("**Bold**");
```
