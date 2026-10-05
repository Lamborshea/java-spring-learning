# L03: 面向对象编程

> 从 JS 开发者视角学习 Java 的 OOP 概念

## 本节课目标

- 理解类与对象的概念
- 掌握封装、继承、多态三大特性
- 学会使用接口和抽象类
- 理解 Java 和 JS 在 OOP 上的差异

## 核心概念

### 1. 类与对象

```javascript
// JavaScript（ES6 类）
class Person {
    constructor(name, age) {
        this.name = name;
        this.age = age;
    }

    greet() {
        return `Hello, I'm ${this.name}`;
    }
}

const john = new Person("John", 25);
john.greet();
```

```java
// Java
public class Person {
    // 字段（属性）
    private String name;
    private int age;

    // 构造方法
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 方法
    public String greet() {
        return "Hello, I'm " + this.name;
    }

    // Getter 和 Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// 使用
Person john = new Person("John", 25);
john.greet();
```

### 2. 封装

- 字段私有化（private）
- 通过 getter/setter 访问
- 保护数据不被随意修改

### 3. 继承

```java
// 父类
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void speak() {
        System.out.println("...");
    }
}

// 子类
public class Dog extends Animal {
    public Dog(String name) {
        super(name);  // 调用父类构造方法
    }

    @Override  // 方法重写注解
    public void speak() {
        System.out.println(name + " says: Woof!");
    }
}
```

### 4. 多态

- 父类引用指向子类对象
- 方法调用看实际类型

```java
Animal animal = new Dog("Buddy");
animal.speak();  // 调用 Dog 的 speak()
```

### 5. 接口 vs 抽象类

| 特性 | 抽象类 | 接口 |
|------|--------|------|
| 方法 | 可以有实现 | JDK 8+ 可以有 default |
| 字段 | 可以有实例字段 | 只能是常量 |
| 继承 | extends（单继承） | implements（多实现） |
| 构造方法 | 可以有 | 不能有 |

```java
// 接口
public interface Drawable {
    void draw();  // 抽象方法

    // JDK 8+ 可以有默认实现
    default void print() {
        System.out.println("Printing...");
    }
}

// 实现类
public class Circle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing circle");
    }
}
```

## 代码示例

查看 [OOPDemo.java](./src/main/java/com/learning/l03/OOPDemo.java) 获取完整代码示例。

## 学习问答（Q&A）

> 以下问题来自真实学习过程中的疑问，附对应代码位置。

### Q1: `public static class Dog extends Animal` 中 class 前面的 `static` 是什么意思？

`static` 表示这是**静态嵌套类**（Nested Class），区别于普通内部类：

1. **不持有外部类实例引用**——直接 `new Dog(...)` 即可，无需先有 `OOPDemo` 实例
2. **能在静态上下文中使用**——`main` 是 static 的，只能直接访问静态成员，所以 `Dog` 必须加 `static` 才能在 `main` 里实例化
3. **语义上就是"定义在另一个类里的顶层类"**——`Dog` 不依赖 `OOPDemo` 的实例状态，只是借它做命名分组（外部以 `OOPDemo.Dog` 引用），加 `static` 最合理也省内存

记忆口诀：**内部类只要不需要访问外部类的实例字段/方法，就应该加 `static`**（IDE 的默认建议）。

### Q2: 抽象方法声明了 `abstract`，子类必须实现，为什么实现时还要加 `@Override`？

`@Override` 不是语法要求，去掉照样编译运行——它是给**编译器和人**看的双重保险：

1. **触发编译器签名核对**：强制验证父类/接口确实存在签名完全一致的方法被覆盖，不匹配立即报错
2. **防止"悄悄通过"的 bug**：若 `Animal.eat()` 是有实现的普通方法，子类手滑写成 `eat(int x)` 会被当作新增重载，编译通过但父类逻辑没被覆盖；标了 `@Override` 则会立刻报错。访问权限写小、返回类型不兼容同理
3. **保护重构**：父类方法改名/删除后，带 `@Override` 的子类实现会马上编译报错提醒，否则变成"孤儿方法"
4. **表达意图**：读代码一眼分清"重写"还是"新增"

原则：**凡是覆盖/实现已有方法，无脑加 `@Override`**。

### Q3: 接口里 `default void print()` 的 `default` 是什么意思？

**Java 8 引入的接口默认方法**：该方法**带实现**，实现类不写也能直接调用（如 `circle.print()`），不满意可重写。

三种方法对比：

```java
public interface Drawable {
    void draw();                     // 抽象方法：实现类必须写
    default void print() { ... }     // 默认方法：自带实现，可选重写
    static void info() { ... }       // 静态方法：只能 Drawable.info() 调用
}
```

**为什么有这个特性**：为了接口演进不破坏现有代码。经典例子是 Java 8 给 `Collection` 加 `stream()`/`sort()`，若是抽象方法则全网实现类瞬间全部编译失败；有了 default，旧代码不改一行自动获得新方法。

注意：`default` 只能用在 interface 里（别和 `switch` 的 `default:` 混淆）；多接口出现同名 default 方法时编译器强制要求重写表态。

### Q4: 抽象类和接口的核心区别是什么？

一句话：**抽象类是"它是什么"（is-a，继承契约），接口是"它能做什么"（can-do，能力契约）**。

| 维度 | 抽象类（如 Animal） | 接口（如 Drawable） |
|---|---|---|
| 继承数量 | 只能 extends 一个 | 可 implements 多个 |
| 成员变量 | 任意字段，有状态 | 只能 `public static final` 常量，无实例状态 |
| 构造方法 | 有（子类 super 调用） | 没有 |
| 方法 | 抽象 + 具体随意混 | 抽象 + default + static |
| 访问修饰 | 可 protected/private | 方法只能 public |
| 语义 | 一族事物的公共骨架 | 附加的能力标签 |

选择：需要共享状态和构造逻辑 → 抽象类；需要被不相关的类共同实现（Comparable、JpaRepository）→ 接口。default 方法普及后**优先接口**，实际开发常"接口定义能力 + 抽象类骨架实现"配合使用（如 `AbstractList implements List`）。

### Q5: `Animal[] animals = {...}` 是数组吗？和 ArrayList 有什么区别？

是 Java 的**数组字面量**写法，等价于 `new Animal[]{...}`。核心特点：

1. **定长**：创建后长度不可变，不能 add/remove
2. **性能最高**：连续内存，无包装开销
3. **协变且运行时检查**：`Dog[]` 可赋给 `Animal[]`，塞错类型运行时抛 `ArrayStoreException`
4. **功能弱**：几乎没有现成方法，要借 `java.util.Arrays`

| | `Animal[]` | `List<Animal>` |
|---|---|---|
| 长度 | 固定 | 动态 |
| API | 几乎没有 | add/remove/stream 全套 |
| 类型安全 | 运行时报错 | 编译期检查 |

对比 JS：JS 的 `[]` 是动态数组，更像 Java 的 `ArrayList`。经验法则：**业务代码一律优先 `List`**，数组只用于定长/性能敏感场景；定长小集合现代写法用 `List.of(...)`。

## 运行方式

```bash
cd L03-oop
mvn compile exec:java -Dexec.mainClass="com.learning.l03.OOPDemo"
```

## 下节课预告

L04: 集合与泛型 - List、Set、Map、泛型通配符
