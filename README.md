# ConfigLib

## Installation

<details>
  <summary>Gradle</summary>
  
  ```groovy
  plugins {
    id "com.github.johnrengelman.shadow" version "6.1.0"   
  }
  ```
  
  ```groovy
  allprojects {
    repositories {
          maven { url 'https://jitpack.io' }
    }
}
  ```
  
  ```groovy
  dependencies {
    implementation 'com.github.TeamKun:ConfigLib:[version]'
}

  ```
  
</details>

<details>
  <summary>Maven</summary>
  
  ```xml
  <repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
  ```
  
  ```xml
  <dependency>
    <groupId>com.github.TeamKun</groupId>
    <artifactId>ConfigLib</artifactId>
    <version>[version]</version>
</dependency>
  ```
  
</details>
