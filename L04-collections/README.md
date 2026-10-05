# L04: 集合与泛型

> 从 JS 开发者视角学习 Java 的集合框架和泛型

## 本节课目标

- 掌握 List、Set、Map 的使用
- 理解泛型的概念和使用
- 学会使用 Stream API 进行函数式操作
- 对比 JS 数组和 Java 集合的区别

## 核心概念

### 1. List（列表）

```javascript
// JavaScript 数组
const arr = [1, 2, 3];
arr.push(4);
arr.map(x => x * 2);    // [2, 4, 6, 8]
arr.filter(x => x > 2); // [3, 4]
```

```java
// Java List（类似数组，但功能更强大）
List<Integer> list = new ArrayList<>();
list.add(1);
list.add(2);
list.add(3);

// Stream API（JDK 8+）
list.stream()
    .map(x -> x * 2)           // map
    .filter(x -> x > 2)       // filter
    .collect(Collectors.toList());
```

### 2. Set（集合）

- 不允许重复元素
- 常用实现：HashSet、TreeSet、LinkedHashSet

### 3. Map（映射）

```javascript
// JavaScript 对象
const obj = { name: "John", age: 25 };
obj.name;       // "John"
obj["name"];    // "John"
```

```java
// Java Map
Map<String, Object> map = new HashMap<>();
map.put("name", "John");
map.put("age", 25);

map.get("name");           // "John"
map.getOrDefault("xxx", "default");
```

### 4. 泛型

```java
// 泛型类
public class Box<T> {
    private T content;

    public T getContent() { return content; }
    public void setContent(T content) { this.content = content; }
}

// 使用
Box<String> stringBox = new Box<>();
stringBox.setContent("Hello");
String str = stringBox.getContent();  // 不需要强制转换
```

## 学习问答（Q&A）

> 以下问题来自真实学习过程中的疑问，附对应代码位置。

### Q1: `new TreeSet<>()` 为什么要加一个 `<>`？

这个 `<>` 叫**菱形操作符（Diamond Operator，Java 7 引入）**。`TreeSet<E>` 是泛型类，创建时必须确定类型参数 `E`：

```java
Set<Integer> treeSet = new TreeSet<Integer>();  // Java 7 前：右边重复写全类型
Set<Integer> treeSet = new TreeSet<>();         // Java 7+：编译器从左边推断 E = Integer
Set<Integer> treeSet = new TreeSet();           // ✗ 裸类型：编译能过但泛型检查整体关闭
```

`<>` 不是"空着"，而是"**类型由上下文推断**"的占位符。不能省略成裸类型：裸类型下往 `Set<Integer>` 里 `add("字符串")` 照样编译通过，取出时才运行时抛 `ClassCastException`，泛型的全部意义消失。

顺带：`TreeSet` 靠元素自身比较规则排序，所以 `E` 必须实现 `Comparable`（`Integer` 是），或构造时传 `Comparator`。Java 10 起可用 `var`，但 `var x = new TreeSet<>()` 不行——左边没有目标类型可供推断。

### Q2: `strings.stream().max(Comparator.comparingInt(String::length))` 怎么解读？

整句语义：**用 Stream 找最长的字符串**。三个零件组合：

1. **`Stream.max(Comparator)`**：max 需要显式提供"怎么比"——参数是 `Comparator<String>`。返回 `Optional<String>`（流可能为空，用 Optional 强制处理"可能没有"），对应下文的 `.orElse(null)`
2. **`Comparator.comparingInt(keyExtractor)`**：比较器工厂——给我一个"从元素提取 int 键"的函数，还你一个"按该键比较"的比较器。这里提取键的是 `String::length`，即按长度比大小
3. **`String::length`（方法引用）**：`(String s) -> s.length()` 的简写

等价写法从啰嗦到简洁：

```java
.max((a, b) -> Integer.compare(a.length(), b.length()))
.max(Comparator.comparing(s -> s.length()))
.max(Comparator.comparingInt(String::length))   // ← 本行
```

注意：并列最长时 `max` 保留**先遇到**的那个。同类高频操作：`Comparator.comparing(Xxx::getField).reversed()`，Spring 项目排序到处是这种链式比较器。

## 运行方式

```bash
cd L04-collections
mvn compile exec:java -Dexec.mainClass="com.learning.l04.CollectionsDemo"
```

## 下节课预告

L05: 函数式编程 - Stream API、方法引用、Lambda 表达式
