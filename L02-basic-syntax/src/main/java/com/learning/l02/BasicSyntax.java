package com.learning.l02;

/**
 * L02: 基础语法与数据类型
 *
 * 本类展示 Java 的基本语法和数据类型，与 JavaScript 进行对比
 * 旨在帮助 JS 开发者快速上手 Java 基础语法
 *
 * @author Java Learning Course
 */
public class BasicSyntax {

    // ===== 1. 基本数据类型 =====

    /**
     * Java 基本数据类型
     *
     * 与 JavaScript 对比：
     * JS: let num = 42; // number 类型
     * Java: int num = 42; // 明确类型
     */
    public static void basicDataTypes() {
        System.out.println("=== 基本数据类型 ===");

        // 整数类型
        byte byteMax = 127;
        short shortMax = 32767;
        int intMax = 2147483647;      // 常用
        long longMax = 9223372036854775807L; // 需要加 L 后缀

        System.out.println("byte: " + byteMax);
        System.out.println("short: " + shortMax);
        System.out.println("int: " + intMax);
        System.out.println("long: " + longMax);

        // 浮点类型
        float floatNum = 3.14f;       // 需要加 f 后缀
        double doubleNum = 3.1415926; // 常用

        System.out.println("float: " + floatNum);
        System.out.println("double: " + doubleNum);

        // 字符和布尔
        char letter = 'A';            // 单引号
        boolean isActive = true;      // 小写

        System.out.println("char: " + letter);
        System.out.println("boolean: " + isActive);
    }

    // ===== 2. 变量与常量 =====

    /**
     * 变量与常量
     *
     * JS: const PI = 3.14;
     * Java: final double PI = 3.14;
     */
    public static void variablesAndConstants() {
        System.out.println("\n=== 变量与常量 ===");

        // 变量声明
        int age = 25;
        String name = "John";

        System.out.println("变量: age = " + age + ", name = " + name);

        // 常量声明（final 关键字）
        final double PI = 3.14159;
        final int MAX_COUNT = 100;
        final String APP_NAME = "MyApp";

        System.out.println("常量: PI = " + PI + ", MAX_COUNT = " + MAX_COUNT);

        // ⚠️ 这行会编译错误：cannot assign a value to final variable
        // PI = 3.14;
    }

    // ===== 3. 字符串处理 =====

    /**
     * 字符串处理
     *
     * JS: const str = "hello".toUpperCase();
     * Java: String str = "hello".toUpperCase();
     */
    public static void stringOperations() {
        System.out.println("\n=== 字符串处理 ===");

        String name = "  Hello Java  ";

        // 大小写转换
        System.out.println("原字符串: '" + name + "'");
        System.out.println("toUpperCase: " + name.toUpperCase().trim());
        System.out.println("toLowerCase: " + name.toLowerCase().trim());

        // 去除首尾空格
        System.out.println("trim: '" + name.trim() + "'");

        // 字符串查找和替换
        String text = "Hello World";
        System.out.println("contains 'World': " + text.contains("World"));
        System.out.println("replace: " + text.replace("World", "Java"));

        // 字符串分割
        String csv = "apple,banana,orange";
        String[] fruits = csv.split(",");
        System.out.print("split: ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();

        // JDK 15+ 文本块
        String sql = """
                SELECT id, name, email
                FROM users
                WHERE status = 'active'
                ORDER BY created_at DESC
                """;
        System.out.println("文本块 SQL:\n" + sql);
    }

    // ===== 4. 运算符 =====

    /**
     * 运算符
     *
     * ⚠️ 重要区别：Java 中 == 比较的是引用，不是值！
     */
    public static void operators() {
        System.out.println("\n=== 运算符 ===");

        // 算术运算符
        int a = 10, b = 3;
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (a / b));       // 整数除法
        System.out.println("a / (double)b = " + (a / (double)b)); // 浮点除法
        System.out.println("a % b = " + (a % b));       // 取余

        // 比较运算符
        System.out.println("\n比较运算符:");
        System.out.println("a == b: " + (a == b));
        System.out.println("a != b: " + (a != b));
        System.out.println("a > b: " + (a > b));
        System.out.println("a < b: " + (a < b));

        // ⚠️ 字符串比较要使用 equals()
        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");

        System.out.println("\n字符串比较:");
        System.out.println("s1 == s2 (可能错误): " + (s1 == s2));     // true（字符串常量池）
        System.out.println("s1 == s3 (错误): " + (s1 == s3));          // false（不同对象）
        System.out.println("s1.equals(s2) (正确): " + s1.equals(s2)); // true
        System.out.println("s1.equals(s3) (正确): " + s1.equals(s3)); // true

        // 逻辑运算符
        boolean x = true, y = false;
        System.out.println("\n逻辑运算符:");
        System.out.println("x && y = " + (x && y));  // false
        System.out.println("x || y = " + (x || y));  // true
        System.out.println("!x = " + (!x));          // false

        // 三元运算符
        int score = 85;
        String grade = score >= 60 ? "及格" : "不及格";
        System.out.println("\n三元运算符: score = " + score + ", grade = " + grade);
    }

    // ===== 5. 控制流 =====

    /**
     * 条件判断
     */
    public static void conditionals() {
        System.out.println("\n=== 条件判断 ===");

        int score = 85;

        // if-else
        if (score >= 90) {
            System.out.println("优秀");
        } else if (score >= 80) {
            System.out.println("良好");
        } else if (score >= 60) {
            System.out.println("及格");
        } else {
            System.out.println("不及格");
        }

        // switch (Java 14+ 箭头表达式)
        String day = "MONDAY";
        String dayType = switch (day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "工作日";
            case "SATURDAY", "SUNDAY" -> "周末";
            default -> "无效日期";
        };
        System.out.println(day + " 是 " + dayType);

        // switch 表达式（返回值）
        int month = 2;
        int days = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> 28;
            default -> 0;
        };
        System.out.println(month + " 月有 " + days + " 天");
    }

    /**
     * 循环
     */
    public static void loops() {
        System.out.println("\n=== 循环 ===");

        // 传统 for 循环
        System.out.print("for 循环 (0-4): ");
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 增强 for 循环（类似 JS 的 for...of）
        String[] names = {"Alice", "Bob", "Charlie"};
        System.out.print("增强 for 循环: ");
        for (String name : names) {
            System.out.print(name + " ");
        }
        System.out.println();

        // while 循环
        int count = 0;
        System.out.print("while 循环: ");
        while (count < 3) {
            System.out.print(count++ + " ");
        }
        System.out.println();

        // do-while（至少执行一次）
        int num = 0;
        System.out.print("do-while 循环: ");
        do {
            System.out.print(num++ + " ");
        } while (num < 3);
        System.out.println();
    }

    // ===== 6. 类型转换 =====

    /**
     * 类型转换
     */
    public static void typeCasting() {
        System.out.println("\n=== 类型转换 ===");

        // 自动类型转换（小 → 大）
        byte b = 10;
        short s = b;   // byte → short
        int i = s;     // short → int
        long l = i;    // int → long
        double d = l;  // long → double

        System.out.println("自动转换: byte(" + b + ") → short(" + s + ") → int(" + i + ") → long(" + l + ") → double(" + d + ")");

        // 强制类型转换（大 → 小）
        double pi = 3.14159;
        int intPi = (int) pi;  // 截断小数部分
        System.out.println("强制转换: double(" + pi + ") → int(" + intPi + ")");

        // 字符串与数值转换
        String strNum = "123";

        // 字符串 → 基本类型
        int parsed = Integer.parseInt(strNum);   // 返回 int
        long parsedLong = Long.parseLong(strNum); // 返回 long

        // 基本类型 → 字符串
        String fromInt = String.valueOf(parsed);  // 推荐
        String fromDouble = Double.toString(3.14);

        System.out.println("parseInt('123'): " + parsed);
        System.out.println("String.valueOf(123): " + fromInt);
    }

    // ===== 主方法 =====

    public static void main(String[] args) {
        System.out.println("========== Java 基础语法 ==========\n");

        basicDataTypes();
        variablesAndConstants();
        stringOperations();
        operators();
        conditionals();
        loops();
        typeCasting();

        System.out.println("\n========== 运行完成 ==========");
    }
}
