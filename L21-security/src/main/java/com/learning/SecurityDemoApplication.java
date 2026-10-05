package com.learning;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * L21: 安全演示
 *
 * 本节课内容：
 * 1. Spring Security 基础配置
 * 2. JWT 生成和验证
 * 3. 密码加密（BCrypt）
 * 4. 认证和授权流程
 *
 * @author Java Learning Course
 */
@Slf4j
@SpringBootApplication
@EnableWebSecurity  // 开启Spring Security
public class SecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoApplication.class, args);
    }

    // ===== 1. Security 配置 =====

    /**
     * Spring Security 配置
     *
     * JS对比: 类似于 middleware 配置
     * app.use(authMiddleware);
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离场景使用JWT，不需要CSRF防护）
            .csrf(csrf -> csrf.disable())

            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers("/api/public/**", "/api/login").permitAll()
                // 需要ADMIN角色的接口
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 其他接口需要登录
                .anyRequest().authenticated()
            )

            // 使用JWT，不使用Session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 添加JWT过滤器（在Spring Security过滤器之前）
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 创建 JWT 过滤器实例
     */
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(new JwtService());
    }

    // ===== 2. JWT 服务 =====

    /**
     * JWT 工具类
     *
     * JS对比: 类似于 JWT 库的封装
     * import jwt from 'jsonwebtoken';
     */
    @Service
    public static class JwtService {

        // JWT密钥 - 生产环境应使用环境变量
        // JS对比: 类似于 JWT_SECRET 环境变量
        private static final String SECRET = "mySecretKey12345678901234567890123456789012345678901234567890";
        private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        // 过期时间（毫秒）- 24小时
        private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

        /**
         * 生成JWT Token
         *
         * @param username 用户名
         * @param roles 角色列表
         * @return JWT Token字符串
         */
        public String generateToken(String username, List<String> roles) {
            // JWT组成：Header.Payload.Signature

            // 1. Header（头部）
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");  // 签名算法
            header.put("typ", "JWT");    // 类型

            // 2. Payload（载荷）- 存放业务数据
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", username);        // 主题（用户标识）
            claims.put("roles", roles);          // 用户角色
            claims.put("iat", System.currentTimeMillis());  // 签发时间
            claims.put("exp", System.currentTimeMillis() + EXPIRATION);  // 过期时间

            // 3. 生成Token
            return Jwts.builder()
                    .header().add(header).and()
                    .claims(claims)
                    .signWith(KEY)  // 使用密钥签名
                    .compact();
        }

        /**
         * 验证JWT Token
         *
         * @param token JWT Token
         * @return 是否有效
         */
        public boolean validateToken(String token) {
            try {
                Jwts.parser()
                        .verifyWith(KEY)
                        .build()
                        .parseSignedClaims(token);
                return true;
            } catch (JwtException e) {
                log.error("JWT验证失败: {}", e.getMessage());
                return false;
            }
        }

        /**
         * 从Token中获取用户名
         */
        public String getUsernameFromToken(String token) {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }

        /**
         * 从Token中获取角色
         */
        @SuppressWarnings("unchecked")
        public List<String> getRolesFromToken(String token) {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("roles", List.class);
        }
    }

    /**
     * JWT 认证过滤器
     *
     * JS对比: 类似于 auth middleware
     * app.use((req, res, next) => {
     *     const token = req.headers.authorization;
     *     // 验证token
     *     next();
     * });
     */
    @Service
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;

        public JwtAuthenticationFilter(JwtService jwtService) {
            this.jwtService = jwtService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            // 从 Authorization 头解析 JWT，验证通过后写入 SecurityContext
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                Authentication auth = getAuthentication(header.substring(7));
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(request, response);
        }

        /**
         * 验证Token并设置认证信息
         */
        public Authentication getAuthentication(String token) {
            if (!jwtService.validateToken(token)) {
                return null;
            }

            String username = jwtService.getUsernameFromToken(token);
            List<String> roles = jwtService.getRolesFromToken(token);

            // 创建UserDetails（简化版）
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();

            UserDetails userDetails = User.builder()
                    .username(username)
                    .password("")  // JWT认证不需要密码
                    .authorities(authorities)
                    .build();

            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);
        }
    }

    // ===== 3. 密码加密服务 =====

    /**
     * 密码服务 - BCrypt加密
     *
     * JS对比: 类似于 bcrypt 库
     * import bcrypt from 'bcrypt';
     */
    @Service
    public static class PasswordService {

        // BCrypt密码编码器
        // JS对比: 类似于 bcrypt.hash(password, saltRounds)
        private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        /**
         * 加密密码
         *
         * BCrypt特点：
         * - 自动生成盐值（salt）
         * - 包含版本、费用因子、实际盐值、哈希值
         * - 单向加密，不可逆
         * - 每次加密结果不同（因为盐值不同）
         *
         * @param rawPassword 原始密码
         * @return 加密后的密码
         */
        public String encodePassword(String rawPassword) {
            return passwordEncoder.encode(rawPassword);
        }

        /**
         * 验证密码
         *
         * @param rawPassword 原始密码
         * @param encodedPassword 加密后的密码
         * @return 是否匹配
         */
        public boolean matches(String rawPassword, String encodedPassword) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }
    }

    // ===== 4. 用户服务 =====

    /**
     * 用户服务 - 演示用户认证
     */
    @Service
    public static class AuthService {

        private final PasswordService passwordService;
        private final JwtService jwtService;

        // 模拟用户数据库（实际应从数据库加载）
        private static final Map<String, UserRecord> USERS = new ConcurrentHashMap<>();

        static {
            // 初始化测试用户（密码都是 "password123"）
            USERS.put("alice", new UserRecord("alice",
                    "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS/lW1pCPBJ3eGVjW",  // BCrypt加密
                    Arrays.asList("ROLE_USER")));

            USERS.put("admin", new UserRecord("admin",
                    "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsS/lW1pCPBJ3eGVjW",  // password123
                    Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
        }

        public AuthService(PasswordService passwordService, JwtService jwtService) {
            this.passwordService = passwordService;
            this.jwtService = jwtService;
        }

        /**
         * 用户登录
         *
         * @param username 用户名
         * @param password 密码
         * @return 登录结果（包含Token）
         */
        public LoginResult login(String username, String password) {
            UserRecord user = USERS.get(username);

            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 验证密码
            if (!passwordService.matches(password, user.encodedPassword)) {
                throw new RuntimeException("密码错误");
            }

            // 生成JWT Token
            List<String> roles = new ArrayList<>();
            for (String role : user.roles) {
                roles.add(role.replace("ROLE_", ""));
            }

            String token = jwtService.generateToken(username, roles);

            return LoginResult.builder()
                    .username(username)
                    .token(token)
                    .roles(roles)
                    .build();
        }

        /**
         * 获取当前登录用户
         */
        public String getCurrentUsername() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : null;
        }

        /**
         * 注册新用户
         */
        public void register(String username, String password) {
            if (USERS.containsKey(username)) {
                throw new RuntimeException("用户名已存在");
            }

            String encodedPassword = passwordService.encodePassword(password);
            USERS.put(username, new UserRecord(username, encodedPassword,
                    Arrays.asList("ROLE_USER")));

            log.info("新用户注册: {}", username);
        }

        static class UserRecord {
            String username;
            String encodedPassword;
            List<String> roles;

            UserRecord(String username, String encodedPassword, List<String> roles) {
                this.username = username;
                this.encodedPassword = encodedPassword;
                this.roles = roles;
            }
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class LoginResult {
            private String username;
            private String token;
            private List<String> roles;
        }
    }

    // ===== 5. UserDetailsService 实现 =====

    /**
     * 自定义UserDetailsService
     *
     * JS对比: 类似于用户仓库接口
     * interface UserRepository {
     *     findByUsername(username: string): Promise<User | null>;
     * }
     */
    @Service
    public static class CustomUserDetailsService implements UserDetailsService {

        private final PasswordService passwordService;

        public CustomUserDetailsService(PasswordService passwordService) {
            this.passwordService = passwordService;
        }

        @Override
        public UserDetails loadUserByUsername(String username) {
            // 实际应从数据库查询
            // 这里使用内存用户做演示

            if ("admin".equals(username)) {
                return User.builder()
                        .username("admin")
                        .password(passwordService.encodePassword("admin123"))
                        .roles("ADMIN", "USER")
                        .build();
            }

            if ("user".equals(username)) {
                return User.builder()
                        .username("user")
                        .password(passwordService.encodePassword("user123"))
                        .roles("USER")
                        .build();
            }

            throw new RuntimeException("用户不存在: " + username);
        }
    }

    // ===== 6. REST 控制器 =====

    @RestController
    @RequestMapping("/api")
    public static class SecurityController {

        private final AuthService authService;
       private final PasswordService passwordService;

        public SecurityController(AuthService authService, PasswordService passwordService) {
            this.authService = authService;
            this.passwordService = passwordService;
        }

        /**
         * 公开接口 - 任何人可访问
         */
        @GetMapping("/public/hello")
        public String publicHello() {
            return "Hello, 这是一个公开接口！";
        }

        /**
         * 登录接口
         */
        @PostMapping("/login")
        public LoginResponse login(@RequestBody LoginRequest request) {
            AuthService.LoginResult result = authService.login(
                    request.getUsername(), request.getPassword());

            return LoginResponse.builder()
                    .token(result.getToken())
                    .username(result.getUsername())
                    .roles(result.getRoles())
                    .build();
        }

        /**
         * 注册接口
         */
        @PostMapping("/register")
        public String register(@RequestBody RegisterRequest request) {
            authService.register(request.getUsername(), request.getPassword());
            return "注册成功";
        }

        /**
         * 用户接口 - 需要USER角色
         */
        @GetMapping("/user/profile")
        public String userProfile() {
            String username = authService.getCurrentUsername();
            return "用户资料: " + username;
        }

        /**
         * 管理员接口 - 需要ADMIN角色
         */
        @GetMapping("/admin/dashboard")
       public String adminDashboard() {
            return "管理员仪表盘";
        }

        /**
         * 测试接口 - 需登录
         */
        @GetMapping("/test")
        public String test() {
            return "测试接口，当前用户: " + authService.getCurrentUsername();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class LoginRequest {
            private String username;
            private String password;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class LoginResponse {
            private String token;
            private String username;
            private List<String> roles;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class RegisterRequest {
            private String username;
            private String password;
        }
    }

    // ===== 7. 演示运行器 =====

    @Bean
    public org.springframework.boot.CommandLineRunner demoRunner(
            PasswordService passwordService,
            JwtService jwtService,
            AuthService authService) {

        return args -> {
            System.out.println("\n========== 安全演示 ==========\n");

            try {
                // 1. 密码加密演示
                demonstratePasswordEncoding(passwordService);

                // 2. JWT生成和验证演示
                demonstrateJwt(jwtService);

                // 3. 登录演示
                demonstrateLogin(authService);

            } catch (Exception e) {
                System.err.println("演示异常: " + e.getMessage());
            }

            System.out.println("\n========== 演示完成 ==========");
            System.out.println("\n可用接口：");
            System.out.println("  POST /api/login - 登录");
            System.out.println("  POST /api/register - 注册");
            System.out.println("  GET /api/public/hello - 公开接口");
            System.out.println("  GET /api/user/profile - 用户接口（需登录）");
            System.out.println("  GET /api/admin/dashboard - 管理员接口（需ADMIN角色）");
        };
    }

    private void demonstratePasswordEncoding(PasswordService passwordService) {
        System.out.println("\n--- 1. BCrypt 密码加密演示 ---");

        String rawPassword = "password123";

        // 加密（多次加密结果不同，因为每次随机生成盐值）
        String encoded1 = passwordService.encodePassword(rawPassword);
        String encoded2 = passwordService.encodePassword(rawPassword);
        String encoded3 = passwordService.encodePassword(rawPassword);

        System.out.println("原始密码: " + rawPassword);
        System.out.println("第1次加密: " + encoded1);
        System.out.println("第2次加密: " + encoded2);
        System.out.println("第3次加密: " + encoded3);
        System.out.println("注意: 每次加密结果不同（因为盐值随机）");

        // 验证（BCrypt会自动提取盐值进行比对）
        boolean match1 = passwordService.matches(rawPassword, encoded1);
        boolean match2 = passwordService.matches(rawPassword, encoded2);
        boolean match3 = passwordService.matches(rawPassword, encoded3);

        System.out.println("验证第1次: " + match1);
        System.out.println("验证第2次: " + match2);
        System.out.println("验证第3次: " + match3);
    }

    private void demonstrateJwt(JwtService jwtService) {
        System.out.println("\n--- 2. JWT 生成和验证演示 ---");

        // 生成Token
        List<String> roles = Arrays.asList("USER", "ADMIN");
        String token = jwtService.generateToken("alice", roles);

        System.out.println("生成的Token: " + token.substring(0, 50) + "...");

        // 验证Token
        boolean valid = jwtService.validateToken(token);
        System.out.println("Token验证结果: " + (valid ? "有效" : "无效"));

        // 解析Token
        String username = jwtService.getUsernameFromToken(token);
        List<String> tokenRoles = jwtService.getRolesFromToken(token);
        System.out.println("Token中的用户名: " + username);
        System.out.println("Token中的角色: " + tokenRoles);
    }

    private void demonstrateLogin(AuthService authService) {
        System.out.println("\n--- 3. 登录演示 ---");

        try {
            // 注册新用户
            authService.register("testuser", "testpass");
            System.out.println("用户注册成功: testuser");

            // 登录（测试密码）
            AuthService.LoginResult result = authService.login("testuser", "testpass");
            System.out.println("登录成功！");
            System.out.println("  用户名: " + result.getUsername());
            System.out.println("  Token: " + result.getToken().substring(0, 30) + "...");
            System.out.println("  角色: " + result.getRoles());

        } catch (Exception e) {
            System.out.println("登录演示: " + e.getMessage());
        }

        // 使用预置用户登录
        try {
            AuthService.LoginResult result = authService.login("alice", "password123");
            System.out.println("\nalice 登录成功！");
            System.out.println("  Token: " + result.getToken().substring(0, 30) + "...");
        } catch (Exception e) {
            System.out.println("alice 登录演示: " + e.getMessage());
        }
    }
}
