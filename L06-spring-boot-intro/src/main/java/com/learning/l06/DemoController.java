package com.learning.l06;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 演示项目结构中常见的几个分层
 *
 * 注意：本课重点是理解项目结构和启动流程，
 * 实际的业务代码会在后续课程中分到 controller/service/repository 三个包
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/greet")
    public Map<String, Object> greet(@RequestParam(defaultValue = "World") String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, " + name + "!");
        result.put("timestamp", LocalDateTime.now().toString());
        return result;
    }
}
