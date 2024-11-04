package dev.oth.gbs.controllers;

import com.google.gson.JsonObject;
import dev.oth.gbs.common.Response;
import dev.oth.gbs.common.Error;
import dev.oth.gbs.domain.TokenDetailModel;
import dev.oth.gbs.domain.User;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.filter.RequiredRole;
import dev.oth.gbs.providers.JwtTokenUtil;
import dev.oth.gbs.interfaces.UserService;
import dev.oth.gbs.domain.TokenModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredRole(UserRole.ROLE_PUBLIC)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/signup")
    public ResponseEntity<Response<User.UserVo>> signup(@RequestBody User.UserSignUpDto userLoginDto) {
        // 회원가입 처리 로직
        User.UserVo createdUser = userService.signup(userLoginDto);
        return Response.<User.UserVo>ok().withData(createdUser).toResponseEntity();
    }

    @Operation(summary = "로그인", description = "사용자 로그인 및 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenModel.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<Response<TokenModel>> login(@RequestBody User.UserLoginDto loginRequest) {
        // 이메일과 비밀번호로 로그인 처리
        User.UserEntity user = userService.validateUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (user != null) {
            TokenModel tokenModel = jwtTokenUtil.issueToken(new TokenDetailModel(user));
            return Response.<TokenModel>ok().withData(tokenModel).toResponseEntity();
        } else {
            return Response.<TokenModel>error(Error.UNAUTHORIZED).toResponseEntity();
        }
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 통해 액세스 토큰을 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenModel.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @RequiredRole(UserRole.ROLE_ANY)
    @PostMapping("/refresh")
    public ResponseEntity<Response<TokenModel>> refreshToken(@RequestBody String refreshToken) {
        try {
            TokenModel tokenModel = jwtTokenUtil.reIssueToken(refreshToken);
            return Response.<TokenModel>ok().withData(tokenModel).toResponseEntity();
        } catch (Exception e) {
            return Response.<TokenModel>error(Error.UNAUTHORIZED).toResponseEntity();
        }
    }

    @Operation(summary = "[테스트]토큰 체크", description = "리프레시 토큰을 통해 액세스 토큰을 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenModel.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @RequiredRole(UserRole.ROLE_ANY)
    // 헤더에서 Authorization 값을 추출하고 리턴하는 메서드
    @GetMapping("/extract-token")
    public ResponseEntity<String> extractTokenFromHeader(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // Bearer 토큰 형태에서 실제 토큰 값만 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);  // "Bearer " 이후의 토큰 부분만 추출
            TokenDetailModel extract = jwtTokenUtil.extractValue(token);
            System.out.println("extract : " + extract);
            boolean validate = jwtTokenUtil.validateToken(token, extract.getId().toString());
            System.out.println("validate = " + validate);
            return ResponseEntity.ok("Extracted Token: " + token);
        }
        return ResponseEntity.badRequest().body("Authorization header is missing or invalid.");
    }
}
