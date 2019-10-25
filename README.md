# MCDiscordReserializer [![Build Status](https://travis-ci.org/Vankka/MCDiscordReserializer.svg?branch=master)](https://travis-ci.org/Vankka/MCDiscordReserializer)
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
        <id>Scarsz-Nexus</id>
        <url>https://nexus.scarsz.me/content/groups/public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.vankka</groupId>
        <artifactId>MCDiscordReserializer</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

#### Gradle
```groovy
repositories {
    maven { url 'https://nexus.scarsz.me/content/groups/public/' }
}

dependencies {
    implementation 'me.vankka:MCDiscordReserializer:2.0.0'
}
```

## How to use
```java
// For Minecraft -> Discord translating
String output = DiscordSerializer.serialize(TextComponent.of("Bold").decoration(TextDecoration.BOLD, true));

// For Discord -> Minecraft translating
TextComponent output = MinecraftSerializer.serialize("**Bold**");
```
