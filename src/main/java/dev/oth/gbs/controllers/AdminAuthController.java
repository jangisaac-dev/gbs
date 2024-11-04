package dev.oth.gbs.controllers;

import dev.oth.gbs.common.Error;
import dev.oth.gbs.common.Response;
import dev.oth.gbs.domain.TokenModel;
import dev.oth.gbs.domain.User;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.filter.RequiredRole;
import dev.oth.gbs.interfaces.UserService;
import dev.oth.gbs.providers.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredRole(UserRole.ROLE_ADMIN)
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "관리자 전용 - 전체 사용자 목록 반환", description = "관리자 권한을 가진 사용자만 전체 사용자 목록을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.UserVo.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다.")
    })
    @GetMapping("/admin/users")
    public ResponseEntity<Response<List<User.UserVo>>> getAllUsers() {
        List<User.UserEntity> users = userService.getAllUsers();
        List<User.UserVo> userListVos = users.stream()
                .map(User.UserEntity::toVo)
                .collect(Collectors.toList());
        return Response.<List<User.UserVo>>ok().withData(userListVos).toResponseEntity();
    }
}
