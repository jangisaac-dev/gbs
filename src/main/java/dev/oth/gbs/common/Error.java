package dev.oth.gbs.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "에러 응답 모델", example = "{ \"code\": 1000, \"message\": \"성공\" }")
public enum Error {
    OK(1000, "성공"),

    CREATE_FAILED(2001, "등록에 실패했습니다."),
    RESOURCE_NOT_FOUND(4004, "데이터를 찾을 수 없습니다."),
    UPDATE_FAILED(2002, "변경에 실패했습니다."),
    DELETE_FAILED(2003, "삭제에 실패했습니다."),

    SQLSyntaxError(3001, "SQL syntax error."),
    InvalidDataAccessApiUsageException(3002, "InvalidDataAccessApiUsage error."),
    DATA_ACCESS_ERROR(3003, "데이터 액세스 오류가 발생했습니다."),
    CONSTRAINT_VIOLATION(3004, "제약 조건 위반 오류가 발생했습니다."),
    DUPLICATE_KEY(3005, "중복된 키가 있습니다."),

    UNAUTHORIZED(4001, "잘못된 로그인 정보입니다."),
    UNAUTHORIZED_NULL(4002, "잘못된 로그인 정보입니다.(NULL)"),
    FORBIDDEN(4003, "권한이 없습니다."),
    SELF_ROLE_ERROR(4003, "권한이 없습니다."),
    BAD_REQUEST(4000, "잘못된 요청입니다."),

    METHOD_NOT_ALLOWED(405, "허용되지 않은 HTTP 메서드입니다."),
    NOT_ACCEPTABLE(406, "요청이 허용되지 않습니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "지원되지 않는 미디어 타입입니다."),

    INTERNAL_SERVER_ERROR(5000, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(5003, "서비스를 사용할 수 없습니다."),

    TIMEOUT(504, "요청 시간이 초과되었습니다."),
    INVALID_ARGUMENT(4005, "잘못된 인수가 제공되었습니다."),
    ILLEGAL_STATE(5004, "잘못된 상태가 감지되었습니다."),
    NULL_POINTER_EXCEPTION(5005, "널 포인터 예외가 발생했습니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(5006, "잘못된 인수 예외가 발생했습니다."),
    IO_EXCEPTION(5007, "I/O 예외가 발생했습니다."),
    NO_HANDLER_FOUND(4044, "요청에 대한 핸들러를 찾을 수 없습니다.");

    @Schema(description = "에러 코드", example = "1000")
    private final int code;

    @Schema(description = "에러 메시지", example = "성공")
    private final String message;
}

