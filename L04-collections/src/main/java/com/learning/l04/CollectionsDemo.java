package com.learning.l04;

import java.util.*;
import java.util.stream.Collectors;

/**
 * L04: 集合与泛型
 *
 * 本类展示 Java 集合框架和泛型的使用，与 JavaScript 数组和对象进行对比
 *
 * @author Java Learning Course
 */
public class CollectionsDemo {

    // ===== 1. List（列表）=====

    /**
     * JavaScript: const arr = [1, 2, 3];
     * Java: List<Integer> list = new ArrayList<>();
     */
    public static void listDemo() {
        System.out.println("=== List 列表 ===");

        // 创建 List（ArrayList 是常用实现）
        List<Integer> list = new ArrayList<>();

        // 添加元素
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(1);  // 允许重复

        System.out.println("原始列表: " + list);
        System.out.println("大小: " +list.size());
        System.out.println("获取第2个元素: " + list.get(1));

        // 修改元素
        list.set(0, 10);
        System.out.println("修改后: " + list);

        // 删除元素
        list.remove(0);  // 按索引删除
        // list.remove(Integer.valueOf(2));  // 按值删除
        System.out.println("删除后: " + list);

        // 遍历方式
        System.out.print("for 遍历: ");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }
        System.out.println();

        System.out.print("增强 for 遍历: ");
        for (int num : list) {
            System.out.print(num + " ");
        }
        System.out.println();

        // 排序
        Collections.sort(list);
        System.out.println("排序后: " + list);

        // JDK 10+ 可以使用 var
        var numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);  // 不可变列表
        System.out.println("List.of (不可变): " + numbers);
    }

    // ===== 2. Set（集合）=====

    /**
     * JavaScript: 无直接对应，可使用 Set 对象（ES6+）
     * const set = new Set([1, 2, 3]);
     */
    public static void setDemo() {
        System.out.println("\n=== Set 集合 ===");

        // 创建 HashSet
        Set<Integer> set = new HashSet<>();

        // 添加元素（重复元素会被忽略）
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(1);  // 重复，被忽略

        System.out.println("Set: " + set);
        System.out.println("大小: " + set.size());

        // 检查元素
        System.out.println("包含 2: " + set.contains(2));
        System.out.println("包含 5: " + set.contains(5));

        // 删除
        set.remove(2);
        System.out.println("删除 2 后: " + set);

        // 遍历
        System.out.print("遍历: ");
        for (int num : set) {
            System.out.print(num + " ");
        }
        System.out.println();

        // LinkedHashSet（保持插入顺序）
        Set<String> linkedSet = new LinkedHashSet<>();
        linkedSet.add("first");
        linkedSet.add("second");
        linkedSet.add("third");
        linkedSet.add("first");  // 重复被忽略
        System.out.println("LinkedHashSet (保持顺序): " + linkedSet);

        // TreeSet（自动排序）
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(3);
        treeSet.add(1);
        treeSet.add(2);
        System.out.println("TreeSet (自动排序): " + treeSet);
    }

    // ===== 3. Map（映射）=====

    /**
     * JavaScript: const obj = { key: "value" };
     * Java: Map<String, String> map = new HashMap<>();
     */
    public static void mapDemo() {
        System.out.println("\n=== Map 映射 ===");

        // 创建 Map
        Map<String, Object> map = new HashMap<>();

        // 添加/修改元素
        map.put("name", "John");
        map.put("age", 25);
        map.put("city", "Beijing");
        map.put("name", "Jane");  // 覆盖原有值

        System.out.println("Map: " + map);
        System.out.println("大小: " + map.size());

        // 获取元素
        System.out.println("name: " + map.get("name"));
        System.out.println("不存在的 key: " + map.get("xxx"));
        System.out.println("默认值: " + map.getOrDefault("xxx", "default"));

        // 检查
        System.out.println("包含 key 'age': " + map.containsKey("age"));
        System.out.println("包含 value 'Beijing': " + map.containsValue("Beijing"));

        // 删除
        map.remove("city");
        System.out.println("删除后: " + map);

        // 遍历
        System.out.println("遍历 keys: " + map.keySet());
        System.out.println("遍历 values: " + map.entrySet());

        System.out.print("遍历 entry: ");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        // JDK 10+ 简洁遍历
        map.forEach((k, v) -> System.out.println(k + " -> " + v));
    }

    // ===== 4. 泛型 =====

    /**
     * 泛型类
     * 类似于 TypeScript 的泛型
     */
    public static class Box<T> {
        private T content;

        public Box(T content) {
            this.content = content;
        }

        public T getContent() {
            return content;
        }

        public void setContent(T content) {
            this.content = content;
        }
    }

    /**
     * 泛型方法
     */
    public static <T> T getFirst(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 泛型通配符
     * ? extends T: 读取 T 及其子类（Producer）
     * ? super T: 写入 T 及其父类（Consumer）
     */
    public static void genericDemo() {
        System.out.println("\n=== 泛型 ===");

        // 使用泛型类
        Box<String> stringBox = new Box<>("Hello");
        System.out.println("String Box: " + stringBox.getContent());

        Box<Integer> intBox = new Box<>(42);
        System.out.println("Integer Box: " + intBox.getContent());

        // 泛型方法
        List<String> names = List.of("Alice", "Bob", "Charlie");
        String first = getFirst(names);
        System.out.println("第一个元素: " + first);

        // 通配符
        List<Number> numbers = List.of(1, 2.5, 3L);
        List<? extends Number> nums = numbers;  // 只读

        System.out.print("读取 Number: ");
        for (Number n : nums) {
            System.out.print(n + " ");
        }
        System.out.println();
    }

    // ===== 5. Stream API（函数式操作）=====

    /**
     * JavaScript 对比:
     * const arr = [1, 2, 3, 4, 5];
     * arr.map(x => x * 2).filter(x => x > 4).reduce((a, b) => a + b, 0);
     */
    public static void streamDemo() {
        System.out.println("\n=== Stream API ===");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // map: 转换
        List<Integer> doubled = numbers.stream()
            .map(x -> x * 2)
            .collect(Collectors.toList());
        System.out.println("原始: " + numbers);
        System.out.println("map(x * 2): " + doubled);

        // filter: 过滤
        List<Integer> evens = numbers.stream()
            .filter(x -> x % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("filter(偶数): " + evens);

        // reduce: 聚合
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);
        System.out.println("reduce(求和): " + sum);

        // findFirst: 查找第一个
        Optional<Integer> firstEven = numbers.stream()
            .filter(x -> x % 2 == 0)
            .findFirst();
        System.out.println("findFirst(第一个偶数): " + firstEven.orElse(null));

        // count: 计数
        long evenCount = numbers.stream()
            .filter(x -> x % 2 == 0)
            .count();
        System.out.println("偶数数量: " + evenCount);

        // anyMatch / allMatch / noneMatch
        System.out.println("anyMatch(>5): " + numbers.stream().anyMatch(x -> x > 5));
        System.out.println("allMatch(<15): " + numbers.stream().allMatch(x -> x < 15));
        System.out.println("noneMatch(<0): " + numbers.stream().noneMatch(x -> x < 0));

        // sorted
        List<Integer> shuffled = List.of(5, 2, 8, 1, 9, 3);
        List<Integer> sorted = shuffled.stream()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("sorted: " + sorted);

        // distinct (去重)
        List<Integer> withDup = List.of(1, 2, 2, 3, 3, 3, 4);
        List<Integer> distinct = withDup.stream()
            .distinct()
            .collect(Collectors.toList());
        System.out.println("distinct: " + distinct);

        // limit / skip
        List<Integer> limited = numbers.stream()
            .limit(5)
            .collect(Collectors.toList());
        System.out.println("limit(5): " + limited);

        // 方法引用（更简洁）
        List<String> words = List.of("hello", "world", "java");
        List<String> upper = words.stream()
            .map(String::toUpperCase)  // 方法引用
            .collect(Collectors.toList());
        System.out.println("方法引用 toUpperCase: " + upper);
    }


    public static void findLongestStr() {
        // List<String> strings = List.of("hello", "world", "java", "stream");
        List<String> strings = new ArrayList<>();
        strings.add("hello");
        strings.add("world");
        strings.add("java");
        strings.add("stream");

        String longest = strings.stream()
            .max(Comparator.comparingInt((String s) -> s.length()))
            .orElse(null);
        System.out.println("最长字符串: " + longest);
    }

    // ===== 主方法 =====

    public static void main(String[] args) {
        System.out.println("========== 集合与泛型 ==========\n");

        listDemo();
        setDemo();
        mapDemo();
        genericDemo();
        streamDemo();
        findLongestStr();
        

        System.out.println("\n========== 运行完成 ==========");
    }
}
