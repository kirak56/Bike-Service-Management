package com.tesla.bikeservices.response;


import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private Map<String, String> errors;
    private Pagination pagination;
    private LocalDateTime timestamp;

    public ApiResponse(String status, String message, T data, Map<String, String> errors, Pagination pagination) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.pagination = pagination;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data, null, null);
    }

    public static <T> ApiResponse<T> successPaginated(String message, T data, Pagination pagination) {
        return new ApiResponse<>("success", message, data, null, pagination);
    }

    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
        return new ApiResponse<>("error", message, null, errors, null);
    }
    
    

    @Data
    public static class Pagination {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public Pagination(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}

