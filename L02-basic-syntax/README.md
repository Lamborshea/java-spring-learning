# L02: 基础语法与数据类型

> 从 JS 开发者视角学习 Java 的基础语法和数据类型

## 本节课目标

- 掌握 Java 基本数据类型
- 理解变量声明与常量的区别
- 学会使用运算符和控制流
- 理解 Java 和 JS 在语法上的关键差异

## 基本数据类型

Java 的基本数据类型（Primitive Types）分为 4 类：

| 分类 | 类型 | 占用空间 | 取值范围 | JS 对比 |
|------|------|---------|---------|--------|
| 整数 | byte | 1 字节 | -128 ~ 127 | 无对应 |
| 整数 | short | 2 字节 | -32768 ~ 32767 | 无对应 |
| 整数 | int | 4 字节 | ±21亿 | number (整数部分) |
| 整数 | long | 8 字节 | 很大很大 | BigInt |
| 浮点 | float | 4 字节 | ±3.4e38 | number |
| 浮点 | double | 8 字节 | ±1.8e308 | number |
| 字符 | char | 2 字节 | 0 ~ 65535 | 无单字符类型 |
| 布尔 | boolean | 1 位 | true/false | boolean |

### 代码示例

```java
// 整数类型
byte b = 127;           // 1 字节
short s = 32767;        // 2 字节
int i = 2147483647;     // 4 字节，默认使用
long l = 9223372036854775807L;  // 8 字节，结尾要加 L

// 浮点类型
float f = 3.14f;        // 4 字节，结尾要加 f
double d = 3.1415926;   // 8 字节，默认使用

// 字符和布尔
char c = 'A';           // 单引号
boolean flag = true;    // 小写
```

## 变量与常量

### JS 中的变量

```javascript
// var: 函数作用域（已不推荐使用）
var oldWay = "不要用";

// let: 块级作用域
let count = 10;

// const: 块级作用域 + 不可重新赋值
const PI = 3.14;
// PI = 3; // 错误！
```

### Java 中的变量

```java
// 变量声明：类型 + 变量名
int age = 25;
String name = "John";

// 常量：final 修饰符
final double PI = 3.14159;
// PI = 3.14; // 编译错误！

// 常量命名规范：全大写，下划线分隔
final int MAX_COUNT = 100;
final String DEFAULT_NAME = "Guest";
```

## 字符串处理

### JS 字符串

```javascript
const name = "John";
const greeting = `Hello, ${name}!`;  // 模板字符串
const upper = name.toUpperCase();
const hasA = name.includes("a");
```

### Java 字符串

```java
String name = "John";

// 字符串拼接
String greeting = "Hello, " + name + "!";

// 格式化（JDK 15+ 文本块）
String formatted = String.format("Hello, %s!", name);

// 常用方法
String upper = name.toUpperCase();     // "JOHN"
boolean hasA = name.contains("a");     // true
String sub = name.substring(0, 2);     // "Jo"
String[] parts = name.split("");       // ["J", "o", "h", "n"]

// JDK 15+ 文本块
String sql = """
    SELECT * FROM users
    WHERE id = 1
    AND status = 'active'
    """;
```

## 运算符

### 与 JS 的对比

```java
// 算术运算符：+ - * / % （与 JS 相同）
int a = 10 / 3;    // 3 (整数除法)
double b = 10.0 / 3;  // 3.333...

// 自增自减：++ -- （与 JS 相同）
int i = 1;
i++;        // 后置自增
++i;        // 前置自增

// 比较运算符：== != > < >= <=
// ⚠️ Java 中 == 比较的是引用地址，不是值！
String s1 = "hello";
String s2 = "hello";
s1 == s2;           // ❌ 可能 false（不同对象）
s1.equals(s2);      // ✅ 比较内容

// 逻辑运算符：&& || ! （与 JS 相同，但不支持 & |）
boolean result = (a > 0) && (b < 100);

// 三元运算符：条件 ? 值1 : 值2 （与 JS 相同）
String status = age >= 18 ? "成年" : "未成年";
```

## 控制流

### 条件判断

```java
// if-else 与 JS 相同
if (age >= 18) {
    System.out.println("成年");
} else if (age >= 12) {
    System.out.println("青少年");
} else {
    System.out.println("儿童");
}

// switch：Java 14+ 支持箭头表达式
String day = "MONDAY";
switch (day) {
    case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> 
        System.out.println("工作日");
    case "SATURDAY", "SUNDAY" -> 
        System.out.println("周末");
    default -> 
        System.out.println("无效日期");
}

// switch 表达式（返回值）
int days = switch (month) {
    case 1, 3, 5, 7, 8, 10, 12 -> 31;
    case 4, 6, 9, 11 -> 30;
    case 2 -> 28;
    default -> 0;
};
```

### 循环

```java
// for 循环
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}

// 增强 for 循环（类似 JS 的 for...of）
String[] names = {"Alice", "Bob", "Charlie"};
for (String name : names) {
    System.out.println(name);
}

// while 循环
int count = 0;
while (count < 5) {
    System.out.println(count++);
}

// do-while 循环（至少执行一次）
do {
    System.out.println("至少执行一次");
} while (false);
```

## 类型转换

### 自动类型转换（隐式）

```java
// 小类型 → 大类型：自动转换
byte b = 10;
int i = b;      // byte → int，自动转换
long l = i;     // int → long，自动转换
double d = l;   // long → double，自动转换
```

### 强制类型转换（显式）

```java
// 大类型 → 小类型：需要强制转换
double d = 3.14;
int i = (int) d;    // 3，丢失小数部分

String s = "123";
int num = Integer.parseInt(s);    // 字符串 → int
String back = String.valueOf(num); // int → 字符串

// ⚠️ 注意：Integer.parseInt() vs Integer.valueOf()
int a = Integer.parseInt("123");   // 返回 int
Integer b = Integer.valueOf("123"); // 返回 Integer（包装类）
```

## 运行方式

```bash
cd L02-basic-syntax
mvn compile exec:java -Dexec.mainClass="com.learning.l02.BasicSyntax"
```

## 下节课预告

L03: 面向对象编程 - 类、对象、继承、封装、多态
