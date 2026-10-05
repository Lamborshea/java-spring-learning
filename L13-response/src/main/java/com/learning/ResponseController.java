package com.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * L13: 响应统一封装演示
 *
 * @author Java Learning Course
 */
@RestController
@RequestMapping("/api/response")
public class ResponseController {

    // ===== 1. 统一响应包装 =====

    /**
     * 统一响应格式
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        public static <T> ApiResponse<T> success(T data) {
            return ApiResponse.<T>builder()
                    .code(200)
                    .message("success")
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        public static <T> ApiResponse<T> error(int code, String message) {
            return ApiResponse.<T>builder()
                    .code(code)
.message(message)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * 普通响应
     */
    @GetMapping("/simple")
    public ApiResponse<String> simpleResponse() {
        return ApiResponse.success("Hello World");
    }

    // ===== 2. 分页响应 =====

    /**
     * 分页响应包装
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
       private int totalPages;
        private boolean first;
        private boolean last;
    }

    /**
     * 分页响应示例
     */
    @GetMapping("/page")
    public ApiResponse<PageResponse<String>> pageResponse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<String> data = List.of("item1", "item2", "item3");
        PageResponse<String> pageData = PageResponse.<String>builder()
                .content(data)
                .page(page)
               .size(size)
                .totalElements(30)
                .totalPages(3)
                .first(page == 0)
               .last(page == 2)
                .build();

        return ApiResponse.success(pageData);
    }

    // ===== 3. 特殊响应 =====

    /**
     * 文件下载响应
     */
    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile() {
        byte[] fileContent = "文件内容".getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "download.txt");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    /**
     * 图片响应
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage() {
        // 实际项目中从文件或数据库读取
        byte[] imageContent = new byte[0];

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageContent);
    }

    /**
     * 自定义状态码响应
     */
    @GetMapping("/custom-status")
    public ResponseEntity<ApiResponse<String>> customStatus() {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.<String>builder()
                        .code(202)
                        .message("Accepted")
                        .data("处理中")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ===== 4. 响应头设置 =====

    /**
     * 设置响应头
     */
    @GetMapping("/with-headers")
    public ResponseEntity<ApiResponse<String>> withHeaders() {
        ApiResponse<String> response = ApiResponse.success("有自定义响应头");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Custom-Header", "custom-value");
        headers.set("X-Rate-Limit", "1000");

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }
}
