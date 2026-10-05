package com.learning.l01;

/**
 * 第一个 Java 程序 - HelloWorld
 *
 * Java 程序入口点（main 方法）的特点：
 * 1. public: 对 JVM 可见
 * 2. static: 可以直接通过类名调用，无需创建对象
 * 3. void: 不返回任何值
 * 4. String[] args: 接收命令行参数，类似 Node.js 的 process.argv
 *
 * 运行方式：
 * 命令行: mvn compile exec:java -Dexec.mainClass="com.learning.l01.HelloWorld"
 * IDEA:   右键 -> Run 'HelloWorld.main()'
 *
 * @author Java Learning Course
 */
public class HelloWorld {

    public static void main(String[] args) {
        // System.out.println() - 输出并换行
        // 类似于 console.log() 在 Node.js 中
        System.out.println("Hello, World!");
        System.out.println("欢迎来到 Java 世界！");

        // ===== 变量与数据类型 =====
        System.out.println("\n=== 变量示例 ===");

        // 基本数据类型（注意：Java 的基本类型有默认值，不是 undefined/null）
        int age = 25;                    // 整型，相当于 JS 的 number（但只支持整数）
        double price = 19.99;            // 双精度浮点，相当于 JS 的 number
        boolean isActive = true;         // 布尔型，相当于 JS 的 true/false
        char grade = 'A';                // 字符型，存储单个字符

        // 引用数据类型
        String name = "Java 开发者";     // 字符串，相当于 JS 的 string
        // 注意：Java 的 String 是对象，有丰富的方法

        // 输出变量
        System.out.println("名字: " + name);
        System.out.println("年龄: " + age);
        System.out.println("价格: " + price);
        System.out.println("活跃: " + isActive);
        System.out.println("等级: " + grade);

        // ===== 格式化输出 =====
        System.out.println("\n=== 格式化输出 ===");

        // printf 类似于 C 语言的 printf 或 JS 的 template literals
        // %s - 字符串, %d - 整数, %.2f - 浮点数(保留2位), %n - 换行
        System.out.printf("我是 %s，今年 %d 岁%n", name, age);
        System.out.printf("价格: $%.2f%n", price);
        System.out.printf("布尔值: %b, 字符: %c%n", isActive, grade);

        // ===== 控制台输入（需要 Scanner 类）=====
        System.out.println("\n=== 输入示例 ===");
        // 注意：实际运行时如果要接收输入，需要使用 Scanner
        // 见 java.util.Scanner 的使用示例

        // ===== 调试技巧 =====
        System.out.println("\n=== 调试技巧 ===");
        String debugMessage = "在 IDEA 中点击左侧行号可以设置断点";
        System.out.println(debugMessage);
        System.out.println("按 F8 单步执行，F9 跳到下一个断点");

        System.out.println("\n=== 程序结束 ===");
    }
}
