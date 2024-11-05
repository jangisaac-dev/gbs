package dev.oth.gbs;

import dev.oth.gbs.common.SelfRoleCheckException;
import dev.oth.gbs.common.Error;
import dev.oth.gbs.common.Response;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import java.sql.SQLSyntaxErrorException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // SQL 구문 오류 처리
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseEntity<Response<Object>> handleSQLSyntaxError(SQLSyntaxErrorException ex) {
        System.out.println("GlobalExceptionHandler: SQLSyntaxErrorException caught - " + ex.getMessage());
        return Response.error(Error.SQLSyntaxError).toResponseEntity();
    }

    // InvalidDataAccessApiUsageException 처리
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Response<Object>> handleInvalidDataAccessApiUsageError(InvalidDataAccessApiUsageException ex) {
        System.out.println("GlobalExceptionHandler: InvalidDataAccessApiUsageException caught - " + ex.getMessage());
        return Response.error(Error.InvalidDataAccessApiUsageException).toResponseEntity();
    }

    // DataAccessException 처리
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Response<Object>> handleDataAccessException(DataAccessException ex) {
        System.out.println("GlobalExceptionHandler: DataAccessException caught - " + ex.getMessage());
        return Response.error(Error.DATA_ACCESS_ERROR).toResponseEntity();
    }

    // 중복 키 예외 처리
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response<Object>> handleDuplicateKeyException(DuplicateKeyException ex) {
        System.out.println("GlobalExceptionHandler: DuplicateKeyException caught - " + ex.getMessage());
        return Response.error(Error.DUPLICATE_KEY).toResponseEntity();
    }

    // 제약 조건 위반 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        System.out.println("GlobalExceptionHandler: ConstraintViolationException caught - " + ex.getMessage());
        return Response.error(Error.CONSTRAINT_VIOLATION).toResponseEntity();
    }

    // MethodArgumentNotValidException 처리 (유효성 검사 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.out.println("GlobalExceptionHandler: MethodArgumentNotValidException caught - " + ex.getMessage());
        return Response.error(Error.INVALID_ARGUMENT).toResponseEntity();
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Response<Object>> handleNullPointerException(NullPointerException ex) {
        System.out.println("GlobalExceptionHandler: NullPointerException caught - " + ex.getMessage());
        return Response.error(Error.NULL_POINTER_EXCEPTION).toResponseEntity();
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.out.println("GlobalExceptionHandler: IllegalArgumentException caught - " + ex.getMessage());
        return Response.error(Error.ILLEGAL_ARGUMENT_EXCEPTION).toResponseEntity();
    }

    // IO Exception 처리
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Response<Object>> handleIOException(IOException ex) {
        System.out.println("GlobalExceptionHandler: IOException caught - " + ex.getMessage());
        return Response.error(Error.IO_EXCEPTION).toResponseEntity();
    }

    // HTTP Method Not Allowed 예외 처리
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response<Object>> handleResponseStatusException(ResponseStatusException ex) {
        System.out.println("GlobalExceptionHandler: Method Not Allowed - " + ex.getMessage());
        return Response.error(Error.METHOD_NOT_ALLOWED).toResponseEntity();
    }

    // NoHandlerFoundException 처리
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<Response<Object>> handleNoHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException ex) {
        System.out.println("GlobalExceptionHandler: NoHandlerFoundException caught - " + ex.getMessage());
        return Response.error(Error.NO_HANDLER_FOUND).toResponseEntity();
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGenericException(Exception ex) {
        System.out.println("GlobalExceptionHandler: Exception caught - " + ex.getMessage());
        return Response.error(Error.INTERNAL_SERVER_ERROR).toResponseEntity();
    }

    //여기는 RoleCheck부분에서 오류 발생시 오는 곳
    @ExceptionHandler(SelfRoleCheckException.class)
    public ResponseEntity<Response<Object>> handleAccessDeniedException(SelfRoleCheckException ex) {

        return Response.error(Error.SELF_ROLE_ERROR).toResponseEntity();
    }
}
