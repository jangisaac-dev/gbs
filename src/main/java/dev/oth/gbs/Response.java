package dev.oth.gbs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonPropertyOrder({"error", "data"})
@Schema(description = "API 응답 모델")
public class Response<T> {

    @Schema(description = "에러 정보")
    private final Error error;

    @Schema(description = "응답 데이터")
    private T data;

    private Response(Error error) {
        this.error = error;
    }

    public static <T> Response<T> ok() {
        return new Response<>(Error.OK);
    }

    public static <T> Response<T> error(Error error) {
        return new Response<>(error);
    }

    public Response<T> withData(T data) {
        this.data = data;
        return this;
    }

    public ResponseEntity<Response<T>> toResponseEntity() {
        return ResponseEntity.ok(this);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public T getData() {
        return this.data;
    }

    public Map<String, Object> getError() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", error.getCode());
        result.put("msg", error.getMessage());
        return result;
    }
}