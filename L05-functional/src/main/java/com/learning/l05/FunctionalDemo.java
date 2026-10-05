package com.learning.l05;

import java.time.LocalDate;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * L05: 函数式编程
 *
 * 本类展示 Java Lambda 表达式、函数式接口和方法引用的使用
 * 与 JavaScript 箭头函数进行对比
 *
 * @author Java Learning Course
 */
public class FunctionalDemo {

    // ===== 1. Lambda 表达式基础 =====

    /**
     * JavaScript 箭头函数对比:
     * const add = (a, b) => a + b;
     * const greet = name => `Hello, ${name}!`;
     * const print = () => console.log("Hello");
     */
    public static void lambdaBasics() {
        System.out.println("=== Lambda 表达式基础 ===");

        // 无参数无返回值 - Runnable
        Runnable runnable = () -> System.out.println("Hello from Lambda!");
        runnable.run();

        // 单参数无返回值 - Consumer
        Consumer<String> consumer = (name) -> System.out.println("Hello, " + name);
        consumer.accept("Java");

        // 双参数有返回值 - BinaryOperator
        BinaryOperator<Integer> add = (a, b) -> a + b;
        System.out.println("3 + 5 = " + add.apply(3, 5));

        // 单参数有返回值 - Function
        Function<String, Integer> strLength = (s) -> s.length();
        System.out.println("'hello' 长度: " + strLength.apply("hello"));

        // 条件判断 - Predicate
        Predicate<Integer> isEven = (n) -> n % 2 == 0;
        System.out.println("4 是偶数: " + isEven.test(4));
        System.out.println("5 是偶数: " + isEven.test(5));

        // 简化语法：单参数可省略括号
        Function<Integer, Integer> doubleIt = n -> n * 2;
        System.out.println("7 * 2 = " + doubleIt.apply(7));

        // 单表达式可省略 return 和大括号
        Supplier<String> supplier = () -> "Hello Supplier";
        System.out.println("Supplier: " + supplier.get());
    }

    // ===== 2. 常用函数式接口 =====

    /**
     * 常用函数式接口速查表
     */
    public static void functionalInterfaces() {
        System.out.println("\n=== 常用函数式接口 ===");

        // Supplier<T> - 无参数，有返回值
        Supplier<LocalDate> dateSupplier = () -> LocalDate.now();
        System.out.println("当前日期: " + dateSupplier.get());

        // Consumer<T> - 有参数，无返回值
        Consumer<String> printer = s -> System.out.println("打印: " + s);
        printer.accept("Hello Consumer");

        // BiConsumer<T,U> - 两个参数，无返回值
        BiConsumer<String, Integer> scorePrinter = (name, score) ->
            System.out.println(name + " 的分数: " + score);
        scorePrinter.accept("Alice", 95);

        // Function<T,R> - 有参数，有返回值
        Function<Integer, String> intToStr = n -> "数字: " + n;
        System.out.println(intToStr.apply(42));

        // BiFunction<T,U,R> - 两个参数，有返回值
        BiFunction<Integer, Integer, Integer> max = (a, b) -> a > b ? a : b;
        System.out.println("max(3, 7) = " + max.apply(3, 7));

        // Predicate<T> - 返回 boolean
        Predicate<String> isEmpty = s -> s.isEmpty();
        System.out.println("'' 是空: " + isEmpty.test(""));
        System.out.println("'hello' 是空: " + isEmpty.test("hello"));

        // UnaryOperator<T> - 同类型输入输出
        UnaryOperator<Integer> square = n -> n * n;
        System.out.println("5 的平方: " + square.apply(5));

        // BinaryOperator<T> - 同类型两个参数，同类型返回值
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        System.out.println("6 * 7 = " + multiply.apply(6, 7));
    }

    // ===== 3. 方法引用 =====

    /**
     * 方法引用 - 更简洁的 Lambda
     *
     * 三种形式:
     * - 对象::实例方法
     * - 类::静态方法
     * - 类::实例方法
     */
    public static void methodReference() {
        System.out.println("\n=== 方法引用 ===");

        // 对象::实例方法
        String str = "Hello";
        Supplier<Integer> lenSupplier = str::length;
        System.out.println("str.length(): " + lenSupplier.get());

        Consumer<String> printer = System.out::println;
        printer.accept("Hello via 方法引用");

        // 类::静态方法
        Function<Double, Long> roundFunc = Math::round;
        System.out.println("round(3.7): " + roundFunc.apply(3.7));

        Function<String, String> upperFunc = String::toUpperCase;
        System.out.println("'hello'.toUpperCase(): " + upperFunc.apply("hello"));

        // 类::实例方法（第一个参数是方法调用者）
        BiFunction<String, String, String> concat = String::concat;
        System.out.println("'Hello'.concat(' World'): " + concat.apply("Hello", " World"));

        // 构造函数引用
        Supplier<ArrayList<String>> listSupplier = ArrayList::new;
        ArrayList<String> list = listSupplier.get();
        list.add("item");
        System.out.println("新 ArrayList: " + list);

        Function<Integer, ArrayList<String>> listWithCap = ArrayList::new;
        ArrayList<String> list2 = listWithCap.apply(10);
        System.out.println("带容量的 ArrayList: " + list2);
    }

    // ===== 4. 复合函数式接口 =====

    /**
     * 函数式接口的默认方法 - 用于组合
     */
    public static void compositeFunctions() {
        System.out.println("\n=== 复合函数式接口 ===");

        // Predicate: and, or, negate
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;

        Predicate<Integer> isPositiveEven = isPositive.and(isEven);
        Predicate<Integer> isNotPositive = isPositive.negate();

        System.out.println("10 > 0 && 是偶数: " + isPositiveEven.test(10));
        System.out.println("10 不大于 0: " + isNotPositive.test(10));

        // Function: andThen, compose
        Function<Integer, Integer> doubleIt = n -> n * 2;
        Function<Integer, Integer> addTen = n -> n + 10;

        Function<Integer, Integer> pipeline1 = doubleIt.andThen(addTen);
        Function<Integer, Integer> pipeline2 = doubleIt.compose(addTen);

        // doubleIt(5) = 10, then addTen(10) = 20
        System.out.println("doubleIt.andThen(addTen)(5): " + pipeline1.apply(5));
        // addTen(5) = 15, then doubleIt(15) = 30
        System.out.println("doubleIt.compose(addTen)(5): " + pipeline2.apply(5));
    }

    // ===== 5. Optional（函数式null处理）=====

    /**
     * JavaScript: const value = obj?.prop ?? 'default';
     * Java: Optional.ofNullable(obj).map(Object::getProp).orElse("default")
     */
    public static void optionalDemo() {
        System.out.println("\n=== Optional ===");

        // 创建 Optional
        Optional<String> emptyOpt = Optional.empty();
        Optional<String> nameOpt = Optional.of("Alice");

        // ofNullable: 允许 null
        String nullStr = null;
        Optional<String> nullableOpt = Optional.ofNullable(nullStr);

        System.out.println("emptyOpt.isPresent(): " + emptyOpt.isPresent());
        System.out.println("nameOpt.isPresent(): " + nameOpt.isPresent());

        // orElse: null 时提供默认值
        String result = Optional.ofNullable(nullStr).orElse("default");
        System.out.println("ofNullable(null).orElse('default'): " + result);

        // orElseGet: 延迟计算
        Supplier<String> supplier = () -> {
            System.out.println("计算默认值...");
            return "computed default";
        };
        String lazyResult = Optional.ofNullable(nullStr).orElseGet(supplier);
        System.out.println("orElseGet: " + lazyResult);

        // map: 转换值
        Optional<Integer> lenOpt = Optional.of("hello")
            .map(String::length);
        System.out.println("'hello'.length(): " + lenOpt.orElse(0));

        // flatMap: 链式 Optional
        Optional<String> nested = Optional.of(Optional.of("nested"))
            .flatMap(Function.identity());
        System.out.println("flatMap 结果: " + nested.orElse("empty"));

        // filter
        Optional<Integer> numOpt = Optional.of(42)
            .filter(n -> n > 10)
            .filter(n -> n < 100);
        System.out.println("filter(10 < n < 100): " + numOpt.orElse(null));
    }

    // ===== 6. Stream 进阶操作 =====

    /**
     * Stream 高级操作
     */
    public static void streamAdvanced() {
        System.out.println("\n=== Stream 进阶 ===");

        // groupingBy - 分组
        List<String> words = List.of("apple", "banana", "apricot", "blueberry", "cherry");
        Map<Character, List<String>> grouped = words.stream()
            .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println("按首字母分组: " + grouped);

        // partitioningBy - 分区
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Map<Boolean, List<Integer>> partitioned = nums.stream()
            .collect(Collectors.partitioningBy(n -> n > 5));
        System.out.println("按是否大于5分区: " + partitioned);

        // joining - 字符串拼接
        String joined = words.stream()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("joining: " + joined);

        // summarizing - 统计
        IntSummaryStatistics stats = nums.stream()
            .collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println("统计: " + stats);
        System.out.println("  平均值: " + stats.getAverage());
        System.out.println("  最大值: " + stats.getMax());
        System.out.println("  最小值: " + stats.getMin());

        // toMap - 转为 Map
        Map<String, Integer> nameToLen = words.stream()
            .collect(Collectors.toMap(Function.identity(), String::length));
        System.out.println("单词 -> 长度: " + nameToLen);

        // 并行流
        long start = System.nanoTime();
        long count = nums.parallelStream()
            .filter(n -> n % 2 == 0)
            .count();
        long end = System.nanoTime();
        System.out.println("并行流统计偶数: " + count + ", 耗时: " + (end - start) + "ns");
    }

    // ===== 主方法 =====

    public static void main(String[] args) {
        System.out.println("========== 函数式编程 ==========\n");

        lambdaBasics();
        functionalInterfaces();
        methodReference();
        compositeFunctions();
        optionalDemo();
        streamAdvanced();

        System.out.println("\n========== 运行完成 ==========");
    }
}
