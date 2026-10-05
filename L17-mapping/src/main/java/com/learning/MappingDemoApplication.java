package com.learning;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * L17: 实体映射与关系演示
 *
 * 本节课内容：
 * 1. @OneToOne - 一对一关系
 * 2. @OneToMany / @ManyToOne - 一对多/多对一关系
 * 3. @ManyToMany - 多对多关系
 * 4. LAZY vs EAGER 加载策略
 *
 * @author Java Learning Course
 */
@SpringBootApplication
public class MappingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MappingDemoApplication.class, args);
    }

    // ===== 1. 一对一关系 (@OneToOne) =====
    // JS对比: 类似于JS对象中的一对一引用，如 user.profile

    /**
     * 用户实体 - 一方
     * 关系: User(1) -> UserProfile(1)
     */
    @Entity
    @Table(name = "t_user")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        private String username;

        // ===== 一对一关系 =====
        // JS对比: 相当于 user.profile = { bio: "...", avatar: "..." }
        // mappedBy: 表示对方是关系的维护方（外键在对方表中）
        // cascade: 连锁操作（删除用户时同时删除档案）
        // fetch: 加载策略，默认 EAGER（因为是一对一，默认立即加载）
        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private UserProfile profile;

        @PrePersist
        protected void onCreate() {
            System.out.println("User: 即将保存 - " + username);
        }

        @PostPersist
        protected void onSaved() {
            System.out.println("User: 已保存 - id=" + id);
        }
    }

    /**
     * 用户档案实体 - 多方（维护外键的一方）
     * 关系: UserProfile(1) -> User(1)
     */
    @Entity
    @Table(name = "t_user_profile")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProfile {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String bio;

        private String avatar;

        // ===== 一对一关系 - 维护方 =====
        // JS对比: 相当于 profile.userId = 123（外键）
        // @JoinColumn: 指定外键列名
        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id", unique = true) // user_id列，唯一约束（保证一对一）
        private User user;
    }

    // ===== 2. 一对多/多对一关系 (@OneToMany / @ManyToOne) =====
    // JS对比: 类似于 user.posts = [post1, post2]（一对多）

    /**
     * 部门实体 - 一方（一个部门有多个员工）
     * 关系: Department(1) -> Employee(Many)
     */
    @Entity
    @Table(name = "t_department")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Department {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 50)
        private String name;

        // ===== 一对多关系 =====
        // JS对比: 相当于 department.employees = [emp1, emp2, ...]
        // cascade: 连锁操作
        // fetch: 默认为 LAZY（一对多通常延迟加载，避免一次性加载大量数据）
        // orphanRemoval: 当从集合中移除实体时，自动删除数据库中的记录
        @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @Builder.Default
        private List<Employee> employees = new ArrayList<>();

        // ===== 便捷方法：双向关系维护 =====
        public void addEmployee(Employee employee) {
            employees.add(employee);
            employee.setDepartment(this);
        }

        public void removeEmployee(Employee employee) {
            employees.remove(employee);
            employee.setDepartment(null);
        }
    }

    /**
     * 员工实体 - 多方（多个员工属于一个部门）
     * 关系: Employee(Many) -> Department(1)
     */
    @Entity
    @Table(name = "t_employee")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Employee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 50)
        private String name;

        private String position;

        // ===== 多对一关系 =====
        // JS对比: 相当于 employee.departmentId = department.id（外键）
        // @JoinColumn: 指定外键列名
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "department_id") // department_id列，指向t_department
        private Department department;

        // ===== 自我引用：员工与经理（自关联的一对多） =====
        // JS对比: employee.manager = managerEmployee
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "manager_id")
        private Employee manager;

        @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
        @Builder.Default
        private List<Employee> subordinates = new ArrayList<>();
    }

    // ===== 3. 多对多关系 (@ManyToMany) =====
    // JS对比: 类似于 student.courses = [course1, course2]，course.students = [s1, s2]

    /**
     * 学生实体
     * 关系: Student(Many) <-> Course(Many)
     */
    @Entity
    @Table(name = "t_student")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Student {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 50)
        private String name;

        private int age;

        // ===== 多对多关系 =====
        // JS对比: student.courses = [c1, c2, c3]
        // 中间表: t_student_course (自动生成)
        // @JoinTable: 指定中间表结构
        @ManyToMany
        @JoinTable(name = "t_student_course", // 中间表名
                joinColumns = @JoinColumn(name = "student_id"), // 当前实体在中间表的外键
                inverseJoinColumns = @JoinColumn(name = "course_id") // 对方实体在中间表的外键
        )
        @Builder.Default
        private List<Course> courses = new ArrayList<>();

        // 双向关系维护
        public void enrollCourse(Course course) {
            courses.add(course);
            course.getStudents().add(this);
        }

        public void dropCourse(Course course) {
            courses.remove(course);
            course.getStudents().remove(this);
        }
    }

    /**
     * 课程实体
     * 关系: Course(Many) <-> Student(Many)
     */
    @Entity
    @Table(name = "t_course")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Course {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true, length = 100)
        private String name;

        private int credits;

        // ===== 多对多关系 - 维护方 =====
        // JS对比: course.students = [s1, s2, s3]
        @ManyToMany(mappedBy = "courses") // 对方是维护方
        @Builder.Default
        private List<Student> students = new ArrayList<>();
    }

    // ===== 4. Repository 接口 =====

    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        // 一对一关系查询
        Optional<User> findByUsername(String username);

        // JPA会自动通过profile查询关联的UserProfile
    }

    @Repository
    public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
        // 根据user查询profile
        Optional<UserProfile> findByUser(User user);

        // 根据userId查询profile（JOIN查询）
        @Query("SELECT up FROM UserProfile up JOIN FETCH up.user u WHERE u.id = :userId")
        Optional<UserProfile> findByUserId(@Param("userId") Long userId);
    }

    @Repository
    public interface DepartmentRepository extends JpaRepository<Department, Long> {
        // 查询部门（同时加载员工 - EAGER方式）
        @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
        Optional<Department> findByIdWithEmployees(@Param("id") Long id);
    }

    @Repository
    public interface EmployeeRepository extends JpaRepository<Employee, Long> {
        // 查询员工（同时加载部门）
        @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id")
        Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

        // 根据部门查询员工
        List<Employee> findByDepartment(Department department);

        // 查询某员工的下属
        List<Employee> findByManager(Employee manager);
    }

    @Repository
    public interface StudentRepository extends JpaRepository<Student, Long> {
        // 查询学生（同时加载选课）
        @Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id")
        Optional<Student> findByIdWithCourses(@Param("id") Long id);

        // 根据课程查询学生
        @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
        List<Student> findByCourseId(@Param("courseId") Long courseId);
    }

    @Repository
    public interface CourseRepository extends JpaRepository<Course, Long> {
        // 查询课程（同时加载学生）
        @Query("SELECT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id")
        Optional<Course> findByIdWithStudents(@Param("id") Long id);

        // 查询学生数超过指定数量的课程
        @Query("SELECT c FROM Course c WHERE SIZE(c.students) > :minCount")
        List<Course> findCoursesWithManyStudents(@Param("minCount") int minCount);
    }

    // ===== 5. Service 层 =====

    @Service
    @Transactional
    public static class RelationDemoService {

        private final UserRepository userRepository;
        private final UserProfileRepository userProfileRepository;
        private final DepartmentRepository departmentRepository;
        private final EmployeeRepository employeeRepository;
        private final StudentRepository studentRepository;
        private final CourseRepository courseRepository;

        public RelationDemoService(
                UserRepository userRepository,
                UserProfileRepository userProfileRepository,
                DepartmentRepository departmentRepository,
                EmployeeRepository employeeRepository,
                StudentRepository studentRepository,
                CourseRepository courseRepository) {
            this.userRepository = userRepository;
            this.userProfileRepository = userProfileRepository;
            this.departmentRepository = departmentRepository;
            this.employeeRepository = employeeRepository;
            this.studentRepository = studentRepository;
            this.courseRepository = courseRepository;
        }

        // ===== 一对一关系示例 =====
        // JS对比: const user = await User.findOne({ where: { username } });
        // const profile = user.profile; // 自动加载

        public void demoOneToOne() {
            System.out.println("\n========== 一对一关系示例 ==========");

            // 创建用户（带档案）
            User user = User.builder()
                    .username("john")
                    .profile(UserProfile.builder()
                            .bio("Java Developer")
                            .avatar("/images/john.png")
                            .user(null) // 先设为null，下面再设置
                            .build())
                    .build();

            // 设置双向关系（重要！）
            user.getProfile().setUser(user);

            // 保存（会自动保存profile，因为cascade = ALL）
            user = userRepository.save(user);
            System.out.println("保存用户: " + user.getUsername() + ", 档案: " + user.getProfile().getBio());

            // 查询用户（EAGER会自动加载profile）
            User found = userRepository.findByUsername("john").orElseThrow();
            System.out.println("查询结果: " + found.getUsername() + ", 档案: " + found.getProfile().getBio());

            // 清理
            userRepository.delete(found);
        }

        // ===== 一对多/多对一关系示例 =====
        // JS对比: const department = await Department.findById(id);
        // const employees = department.employees; // 延迟加载

        public void demoOneToMany() {
            System.out.println("\n========== 一对多/多对一关系示例 ==========");

            // 创建部门
            Department dept = Department.builder()
                    .name("技术部")
                    .build();
            dept = departmentRepository.save(dept);

            // 创建员工并关联部门
            Employee emp1 = Employee.builder()
                    .name("张三")
                    .position("Java开发")
                    .department(dept)
                    .build();

            Employee emp2 = Employee.builder()
                    .name("李四")
                    .position("前端开发")
                    .department(dept)
                    .build();

            employeeRepository.save(emp1);
            employeeRepository.save(emp2);

            System.out.println("已创建部门: " + dept.getName() + ", 员工数: " + dept.getEmployees().size());

            // 查询部门（LAZY，不会自动加载员工）
            Department found = departmentRepository.findById(dept.getId()).orElseThrow();
            System.out.println("查询部门: " + found.getName());

            // 访问员工列表时才加载（懒加载）
            System.out.println("员工列表:");
            for (Employee emp : found.getEmployees()) {
                System.out.println("  - " + emp.getName() + ": " + emp.getPosition());
            }

            // 清理
            employeeRepository.delete(emp1);
            employeeRepository.delete(emp2);
            departmentRepository.delete(dept);
        }

        // ===== 多对多关系示例 =====
        // JS对比: const student = await Student.findById(id).include('courses');
        // const courses = student.courses;

        public void demoManyToMany() {
            System.out.println("\n========== 多对多关系示例 ==========");

            // 创建课程
            Course javaCourse = Course.builder()
                    .name("Java编程")
                    .credits(4)
                    .build();
            Course dbCourse = Course.builder()
                    .name("数据库原理")
                    .credits(3)
                    .build();

            javaCourse = courseRepository.save(javaCourse);
            dbCourse = courseRepository.save(dbCourse);

            // 创建学生并选课
            Student student = Student.builder()
                    .name("王五")
                    .age(20)
                    .build();

            // 通过便捷方法建立关系
            student.enrollCourse(javaCourse);
            student.enrollCourse(dbCourse);

            student = studentRepository.save(student);

            System.out.println("学生: " + student.getName() + ", 选课数: " + student.getCourses().size());

            // 查询课程的学生
            Course java = courseRepository.findById(javaCourse.getId()).orElseThrow();
            System.out.println("课程: " + java.getName() + ", 选课学生: " + java.getStudents().size());

            // 清理
            studentRepository.delete(student);
            courseRepository.delete(javaCourse);
            courseRepository.delete(dbCourse);
        }

        // ===== 加载策略对比 =====
        // LAZY（延迟加载）: 访问关联对象时才查询
        // 优点: 减少不必要的数据库查询
        // 缺点: 可能导致 N+1 问题
        // EAGER（立即加载）: 查询主对象时同时查询关联对象
        // 优点: 避免N+1问题
        // 缺点: 可能会加载不需要的数据

        public void demoFetchStrategy() {
            System.out.println("\n========== 加载策略示例 ==========");

            // 创建测试数据
            Department dept = Department.builder().name("测试部").build();
            dept = departmentRepository.save(dept);

            Employee emp = Employee.builder()
                    .name("测试员工")
                    .position("Tester")
                    .department(dept)
                    .build();
            employeeRepository.save(emp);

            // LAZY加载演示
            System.out.println("1. 使用 LAZY 加载（默认一对多）:");
            Department d1 = departmentRepository.findById(dept.getId()).orElseThrow();
            System.out.println("  部门已查询，此时不查询员工列表");
            // 实际访问employees时才触发SQL
            System.out.println("  员工数: " + d1.getEmployees().size());

            // EAGER加载演示
            System.out.println("2. 使用 EAGER 加载（默认一对一）:");
            User user = User.builder().username("test").build();
            user = userRepository.save(user);
            UserProfile profile = UserProfile.builder().bio("test").user(user).build();
            userProfileRepository.save(profile);

            User u2 = userRepository.findByUsername("test").orElseThrow();
            // EAGER会自动加载profile，不需要额外访问
            System.out.println("  用户: " + u2.getUsername() + ", 档案: " + u2.getProfile().getBio());

            // 清理
            employeeRepository.delete(emp);
            departmentRepository.delete(dept);
            userProfileRepository.delete(profile);
            userRepository.delete(user);
        }

        // ===== 运行所有演示 =====
        public void runAllDemos() {
            try {
                demoOneToOne();
                demoOneToMany();
                demoManyToMany();
                demoFetchStrategy();
                System.out.println("\n========== 所有演示完成 ==========");
            } catch (Exception e) {
                System.err.println("演示出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ===== 6. 配置说明 =====
    /*
     * JPA关系映射核心注解：
     *
     * @OneToOne 一对一关系
     * 
     * @OneToMany 一对多关系（只能放在Collection/Map上）
     * 
     * @ManyToOne 多对一关系
     * 
     * @ManyToMany 多对多关系
     *
     * 常用属性：
     * - targetEntity: 关联的目标实体类型
     * - cascade: 级联操作（CascadeType.PERSIST, REMOVE, MERGE, ALL）
     * - fetch: 加载策略（FetchType.LAZY, EAGER）
     * - mappedBy: 指定对方是关系的维护方
     * - @JoinColumn: 指定外键列
     * - @JoinTable: 指定多对多中间表
     *
     * LAZY vs EAGER 默认值：
     * - @OneToOne: 默认 EAGER
     * - @ManyToOne: 默认 EAGER
     * - @OneToMany: 默认 LAZY
     * - @ManyToMany: 默认 LAZY
     */
}
