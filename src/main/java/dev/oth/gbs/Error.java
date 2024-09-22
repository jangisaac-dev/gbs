package dev.oth.gbs;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true, description = "에러 모델")
public enum Error {
    OK(1000, "성공"),

    RESOURCE_NOT_FOUND(4004, "데이터를 찾을 수 없습니다."),

    CREATE_FAILED(2001, "등록에 실패했습니다."),
    UPDATE_FAILED(2002, "변경에 실패했습니다."),
    DELETE_FAILED(2003, "삭제를 실패했습니다.");


    @Schema(description = "에러 코드")
    private final int code;

    @Schema(description = "에러 메시지")
    private final String message;

}