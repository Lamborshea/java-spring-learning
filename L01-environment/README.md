# L01: 开发环境搭建

> 安装 JDK + Maven + IDEA，运行第一个 Java 程序

## 环境要求

- JDK 17+ (LTS 版本)
- Maven 3.8+
- IntelliJ IDEA 2023+ (社区版免费)

## Windows 安装

### 1. 安装 JDK

```powershell
# 方法一：使用 Winget (推荐)
winget install Oracle.JDK.17

# 方法二：手动下载
# 访问 https://www.oracle.com/java/technologies/downloads/#java17
# 下载 JDK 17 Windows x64 Installer
# 运行安装程序，记住安装路径（如 C:\Program Files\Java\jdk-17）
```

### 2. 配置环境变量

```powershell
# 打开"系统属性" -> "高级" -> "环境变量"
# 新建系统变量:
#   JAVA_HOME = C:\Program Files\Java\jdk-17
#   MAVEN_HOME = C:\apache-maven-3.9.x

# 编辑 Path 变量，添加:
#   %JAVA_HOME%\bin
#   %MAVEN_HOME%\bin
```

### 3. 验证安装

```powershell
java -version
mvn -version
```

## Mac 安装

### 1. 安装 JDK

```bash
# 方法一：使用 Homebrew (推荐)
brew install openjdk@17

# 方法二：下载安装
# 访问 https://www.oracle.com/java/technologies/downloads/#java17
# 下载 JDK 17 macOS x64 DMG Installer
```

### 2. 配置环境变量

```bash
# 添加到 ~/.zshrc 或 ~/.bash_profile
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

# 生效
source ~/.zshrc
```

### 3. 安装 Maven

```bash
# Homebrew
brew install maven

# 验证
mvn -version
```

## IDE 安装

### IntelliJ IDEA (推荐)

```bash
# Mac
brew install --cask intellij-idea

# Windows
winget install JetBrains.IntelliJIDEA.Community
```

### VS Code (轻量替代)

```bash
# 安装 VS Code
brew install --cask visual-studio-code

# 安装扩展
# - Extension Pack for Java
# - Maven for Java
```

## 第一个 Java 程序

### 项目结构

```
L01-environment/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── l01/
                        └── HelloWorld.java
```

### 代码

```java
package com.learning.l01;

/**
 * 第一个 Java 程序
 *
 * 运行方式：
 * 1. 命令行: mvn compile exec:java -Dexec.mainClass="com.learning.l01.HelloWorld"
 * 2. IDEA: 右键 -> Run 'HelloWorld.main()'
 */
public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello, World!");
        System.out.println("欢迎来到 Java 世界！");

        // 带格式的输出
        String name = "Java 开发者";
        int year = 2024;
        System.out.printf("我是 %s，正在学习 Java%n", name);
        System.out.printf("今年是 %d 年%n", year);

        // 调试技巧：在 IDEA 中设置断点，点击调试按钮
        String message = "在这里设置断点";
        System.out.println(message);
    }
}
```

## 常见问题

### Q: java 命令找不到

检查 JAVA_HOME 是否正确配置：
```bash
# Windows
echo %JAVA_HOME%

# Mac/Linux
echo $JAVA_HOME
```

### Q: Maven 下载依赖慢

修改 Maven 镜像源为阿里云：

```xml
<!-- pom.xml 中添加 -->
<repositories>
    <repository>
        <id>aliyun</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

### Q: IDEA 找不到 JDK

1. File → Project Structure → Platform Settings → SDKs
2. 点击 + → Add JDK
3. 选择 JDK 安装目录

## 下节课预告

L02: 基础语法与数据类型 - 变量、常量、运算符、控制流
