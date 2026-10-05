package com.learning.l03;

/**
 * L03: 面向对象编程
 *
 * 本类展示 Java 面向对象编程的核心概念：封装、继承、多态、接口
 * 与 JavaScript 的 class 进行对比
 *
 * @author Java Learning Course
 */
public class OOPDemo {

    // ===== 1. 类与对象 =====

    /**
     * JavaScript 类（对比参考）
     * class Person {
     *     constructor(name, age) {
     *         this.name = name;
     *         this.age = age;
     *     }
     *     greet() { return `Hello, I'm ${this.name}`; }
     * }
     */

    /**
     * Java 类定义
     *
     * 与 JS 对比：
     * - 字段必须声明类型
     * - 方法必须声明返回类型
     * - 构造方法与类名相同，无返回类型
     * - 使用 private 实现封装
     */
    public static class Person {
        // 字段（属性）- private 实现封装
        private String name;
        private int age;

        // 构造方法 1：无参构造
        public Person() {
        }

        // 构造方法 2：带参构造（重载）
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Getter 方法
        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        // Setter 方法
        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            if (age < 0) {
                throw new IllegalArgumentException("年龄不能为负数");
            }
            this.age = age;
        }

        // 普通方法
        public String greet() {
            return "Hello, I'm " + name + ", " + age + " years old";
        }

        // toString 方法
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    // ===== 2. 继承 =====

    /**
     * 父类：动物
     * 使用 protected 让子类可以访问
     */
    public static abstract class Animal {
        protected String name;

        public Animal(String name) {
            this.name = name;
        }

        // 抽象方法：子类必须实现
        public abstract void speak();

        // 普通方法
        public void eat() {
            System.out.println(name + " is eating");
        }
    }

    /**
     * 子类：狗
     * 使用 extends 继承
     */
    public static class Dog extends Animal {
        private String breed;  // 品种

        public Dog(String name, String breed) {
            super(name);  // 调用父类构造方法
            this.breed = breed;
        }

        // 重写父类方法
        @Override
        public void speak() {
            System.out.println(name + " says: Woof!");
        }

        // 子类特有方法
        public void fetch() {
            System.out.println(name + " is fetching the ball");
        }

        @Override
        public String toString() {
            return "Dog{name='" + name + "', breed='" + breed + "'}";
        }
    }

    /**
     * 子类：猫
     */
    public static class Cat extends Animal {
        public Cat(String name) {
            super(name);
        }

        @Override
        public void speak() {
            System.out.println(name + " says: Meow!");
        }
    }

    // ===== 3. 接口 =====

    /**
     * 接口定义
     * 类似于 TypeScript 的 interface
     * 但 Java 接口可以有 default 方法（JDK 8+）
     */
    public interface Drawable {
        // 抽象方法（必须实现）
        void draw();

        // 默认方法（可选重写）
        default void print() {
            System.out.println("Printing drawable...");
        }

        // 静态方法
        static void info() {
            System.out.println("Drawable interface");
        }
    }

    /**
     * 接口实现类
     */
    public static class Circle implements Drawable {
        private double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public void draw() {
            System.out.println("Drawing circle with radius " + radius);
        }

        public double getArea() {
            return Math.PI * radius * radius;
        }
    }

    /**
     * 矩形实现
     */
    public static class Rectangle implements Drawable {
        private double width;
        private double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void draw() {
            System.out.println("Drawing rectangle " + width + "x" + height);
        }
    }

    // ===== 4. 多态演示 =====

    /**
     * 多态：父类引用指向子类对象
     *
     * JS 中的多态：
     * const animals = [new Dog(), new Cat()];
     * animals.forEach(a => a.speak());
     */
    public static void demonstratePolymorphism() {
        System.out.println("\n=== 多态演示 ===");

        // 父类引用指向子类对象
        Animal animal1 = new Dog("Buddy", "Golden Retriever");
        Animal animal2 = new Cat("Whiskers");

        // 调用的是实际类型的方法
        animal1.speak();  // 调用 Dog 的 speak
        animal2.speak();  // 调用 Cat 的 speak

        // 数组中的多态
        Animal[] animals = {
            new Dog("Rex", "German Shepherd"),
            new Cat("Tom"),
            new Dog("Max", "Poodle")
        };

        System.out.println("\n所有动物发声:");
        for (Animal animal : animals) {
            animal.speak();  // 运行时确定调用哪个版本
        }
    }

    // ===== 5. 接口多态 =====

    /**
     * 接口多态
     */
    public static void demonstrateInterfacePolymorphism() {
        System.out.println("\n=== 接口多态 ===");

        Drawable[] shapes = {
            new Circle(5.0),
            new Rectangle(4.0, 6.0),
            new Circle(2.5)
        };

        for (Drawable shape : shapes) {
            shape.draw();
        }
    }

    // ===== 主方法 =====

    public static void main(String[] args) {
        System.out.println("========== 面向对象编程 ==========\n");

        // 1. 类与对象
        System.out.println("=== 1. 类与对象 ===");
        Person person = new Person("John", 25);
        System.out.println(person);
        System.out.println(person.greet());

        // 修改属性（通过 setter）
        person.setAge(26);
        System.out.println("年龄更新为: " + person.getAge());

        // 2. 继承
        System.out.println("\n=== 2. 继承 ===");
        Dog dog = new Dog("Max", "Labrador");
        System.out.println(dog);
        dog.speak();  // 调用重写的方法
        dog.eat();    // 继承的方法
        dog.fetch();  // 子类特有方法

        // 3. 接口
        System.out.println("\n=== 3. 接口 ===");
        Circle circle = new Circle(3.0);
        circle.draw();
        circle.print();  // 使用默认方法

        Drawable.info();  // 调用静态方法

        // 4. 多态
        demonstratePolymorphism();

        // 5. 接口多态
        demonstrateInterfacePolymorphism();

        System.out.println("\n========== 运行完成 ==========");
    }
}
