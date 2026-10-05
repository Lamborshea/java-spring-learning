# L05: 函数式编程

> 从 JS 开发者视角学习 Java 的 Lambda、方法引用和函数式接口

## 本节课目标

- 掌握 Lambda 表达式的语法
- 理解函数式接口
- 学会使用方法引用
- 掌握 Stream API 进阶操作

## 核心概念

### 1. Lambda 表达式

```javascript
// JavaScript 箭头函数
const add = (a, b) => a + b;
const greet = name => `Hello, ${name}!`;
const print = () => console.log("Hello");
```

```java
// Java Lambda 表达式
// 完整语法：(参数) -> { 方法体 }
BinaryOperator<Integer> add = (a,b) -> a + b;
Consumer<String> greet = name -> System.out.println("Hello, " + name);
Runnable print = () -> System.out.println("Hello");

// 单参数可以省略括号
Function<String, Integer> strLen = s -> s.length();

// 单表达式可以省略 return 和大括号
Supplier<String> supplier = () -> "hello";
```

### 2. 函数式接口

只有一个抽象方法的接口，称为函数式接口。

常用函数式接口：

| 接口 | 抽象方法 | 用途 |
|------|----------|------|
| Runnable | void run() | 无参数无返回值 |
| Supplier<T> | T get() | 无参数有返回值 |
| Consumer<T> | void accept(T t) | 有参数无返回值 |
| Function<T,R> | R apply(T t) | 有参数有返回值 |
| Predicate<T> | boolean test(T t) | 条件判断 |
| BiFunction<T,U,R> | R apply(T t, U u) | 两个参数 |

### 3. 方法引用

```java
// 语法：对象::方法 或 类::方法
Function<String, String> toUpper = String::toUpperCase;
Consumer<String> printer = System.out::println;
Supplier<LocalDate> dateSupplier = LocalDate::now;

// 构造函数引用
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
Function<Integer, ArrayList<String>> listWithCap = ArrayList::new;
```

## 学习问答（Q&A）

> 以下问题来自真实学习过程中的疑问，附对应代码位置（FunctionalDemo.java）。

### Q1: `Optional.of` 允许值为 null 吗？

**不允许**，`Optional.of(null)` 直接抛 `NullPointerException`——`of` 的语义是"我确认这个值非空"。三个工厂方法分工：

```java
Optional.of(value)         // value 为 null → 立刻抛 NPE
Optional.ofNullable(value) // null → Optional.empty()，安全
Optional.empty()           // 手动构造空
```

对照本文件：处理可能为 null 的 `nullStr` 用的是 `Optional.ofNullable(nullStr)`（orElseGet 示例处）；而传入字面量 `"hello"`、`42` 的确定非空，用 `of` 没问题。**实际开发规则：来源不可控就一律 `ofNullable`**——如 JPA 的 `findById`、HTTP header 取值。用 `of` 传了 null 等于让 Optional 自己成为异常源，与其"消灭空指针"的初衷完全相反。

### Q2: Optional 是不是就相当于手动写 `if (value != null) {}`？

效果等价，但思路不同——它不是"帮你判空"，而是**换一种写法**：

```java
// 写法 A：判 null（命令式）
if (user != null) {
    String city = user.getAddress() != null ? user.getAddress().getCity() : null;
    if (city != null) { System.out.println(city.toUpperCase()); }
}

// 写法 B：Optional 链（声明式）——判空逻辑下沉进 map："空则跳过，有值才执行"
Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .map(String::toUpperCase)
    .ifPresent(System.out::println);
```

真正价值：1) 把"可能没有"**写进方法签名的类型里**（`findById` 返回 `Optional<User>` 而非 `User`，编译器替你记事）；2) 多层嵌套判空压平成一条管道；3) 表达"缺失是正常情况"。

反面用法（等于没用 Optional）：

```java
if (opt.isPresent()) { use(opt.get()); }   // ✗ 脱裤子放屁，还是判 null
opt.get().toUpperCase();                    // ✗ 空时抛 NoSuchElementException，换个姿势的 NPE
opt.map(String::toUpperCase).orElse("default");  // ✓ "或"语义
```

类比 JS：最接近可选链 `user?.address?.city ?? default`；区别是 `?.` 只防读取时炸，Optional 让类型本身携带"可能没有"。单点临时判空 `if != null` 更直白；Optional 主场是**返回值契约 + 链式转换**。

### Q3: 链式写法例子里没有出现 `Optional` 字样，它从哪来的？

因为链的**源头方法本身返回 `Optional`**，只是类型没显式写出来。以 Spring Data JPA 为例，`findById` 的签名就是 `Optional<T> findById(ID)`：

```java
Optional<User> found = repository.findById(id);   // ← Optional 在这里诞生

found                              // Optional<User>
    .map(user -> user.getAddress())     // 返回 Optional<Address>
    .map(addr -> addr.getCity())        // 返回 Optional<String>
    .ifPresent(System.out::println);
```

Optional 对象只有两个来源：**别人给的**（库/框架方法返回它，最常见，如 `findById` 查不到返回 `Optional.empty()`）和**自己造的**（`of`/`ofNullable`/`empty`，通常在你实现一个返回 Optional 的方法时）。`orElse(null)` 可把 Optional 退化成裸对象，但一旦拿出来，保护就消失了。链式写法里看不到 `Optional` 字样，是因为它藏在第一个方法的返回类型里，`map` 的返回值仍是 `Optional`，只是没人显式写类型。

## 运行方式

```bash
cd L05-functional
mvn compile exec:java -Dexec.mainClass="com.learning.l05.FunctionalDemo"
```

## 下节课预告

L06: Spring Boot 入门 - 第一个 Spring Boot 项目，跑通 REST 接口
