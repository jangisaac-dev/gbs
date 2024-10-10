package dev.oth.gbs.controllers;

import dev.oth.gbs.common.Response;
import dev.oth.gbs.common.Error;
import dev.oth.gbs.domain.User;
import dev.oth.gbs.providers.JwtTokenUtil;
import dev.oth.gbs.interfaces.UserService;
import dev.oth.gbs.domain.TokenModel;
import org.springframework.beans.factory.annotation.Autowired;
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
            TokenModel tokenModel = jwtTokenUtil.issueToken(user.getId(), user.getEmail());
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
    @PostMapping("/refresh")
    public ResponseEntity<Response<TokenModel>> refreshToken(@RequestBody String refreshToken) {
        try {
            TokenModel tokenModel = jwtTokenUtil.reIssueToken(refreshToken);
            return Response.<TokenModel>ok().withData(tokenModel).toResponseEntity();
        } catch (Exception e) {
            return Response.<TokenModel>error(Error.UNAUTHORIZED).toResponseEntity();
        }
    }


    @Operation(summary = "관리자 전용 - 전체 사용자 목록 반환", description = "관리자 권한을 가진 사용자만 전체 사용자 목록을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.UserVo.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // ADMIN 권한이 있는 사용자만 접근 가능
    @GetMapping("/admin/users")
    public ResponseEntity<Response<List<User.UserVo>>> getAllUsers() {
        List<User.UserEntity> users = userService.getAllUsers();
        List<User.UserVo> userListVos = users.stream()
                .map(User.UserEntity::toVo)
                .collect(Collectors.toList());
        return Response.<List<User.UserVo>>ok().withData(userListVos).toResponseEntity();
    }
}
