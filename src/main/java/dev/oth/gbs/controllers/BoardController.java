package dev.oth.gbs.controllers;


import dev.oth.gbs.common.Response;
import dev.oth.gbs.common.Error;
import dev.oth.gbs.domain.Board;
import dev.oth.gbs.domain.TokenDetailModel;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.filter.RequiredRole;
import dev.oth.gbs.interfaces.BoardService;
import dev.oth.gbs.providers.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredRole(UserRole.ROLE_ANY)
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "게시물 생성", description = "새로운 게시물을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Response<Board.BoardDto>> createBoard(HttpServletRequest request, @RequestBody Board.BoardDto boardDto) {
        TokenDetailModel tokenData = jwtTokenUtil.getTokenDataFromRequest(request);
        if (tokenData == null) {
            return Response.<Board.BoardDto>error(Error.UNAUTHORIZED).toResponseEntity();
        }

        Long userId = tokenData.getId(); // 토큰에서 userId 추출
        Board.BoardDto createdBoard = boardService.createBoard(boardDto.toCreateDao(userId));
        return Response.<Board.BoardDto>ok().withData(createdBoard).toResponseEntity();
    }

    @Operation(summary = "게시물 조회", description = "ID로 게시물을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response<Board.BoardDetailVo>> getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id)
                .map(boardDto -> Response.<Board.BoardDetailVo>ok().withData(boardDto).toResponseEntity())
                .orElse(Response.<Board.BoardDetailVo>error(Error.RESOURCE_NOT_FOUND).toResponseEntity());
    }

    @Operation(summary = "모든 게시물 조회", description = "게시물 리스트를 가져옵니다.")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
    })
    public ResponseEntity<Response<List<Board.BoardListVo>>> getAllBoards(
            @Parameter(description = "페이지 번호 (1부터 시작), page < 0 = 1", example = "1")
            @Nullable @Param("page") Integer page,
            @Parameter(description = "한 페이지에 표시할 게시물 수, size < 1 = 1", example = "10")
            @Nullable @Param("size") Integer size
    ) {
        List<Board.BoardListVo> boards;
        if (page == null || size == null) {
            boards = boardService.getAllBoards();
        }
        else {
            page = Math.max(page, 1);
            size = Math.max(size, 1);
            boards = boardService.getBoards(page, size);
        }
        return Response.<List<Board.BoardListVo>>ok().withData(boards).toResponseEntity();
    }

    @Operation(summary = "게시물 수정", description = "ID로 게시물을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다."),
    })
    @RequiredRole({UserRole.ROLE_SELF, UserRole.ROLE_ADMIN})
    @PutMapping("/{id}")
    public ResponseEntity<Response<Board.BoardDto>> updateBoard(@PathVariable Long id, @RequestBody Board.BoardDto boardDto) {
        try {
            Board.BoardDto updatedBoard = boardService.updateBoard(id, boardDto);
            return Response.<Board.BoardDto>ok().withData(updatedBoard).toResponseEntity();
        } catch (RuntimeException e) {
            return Response.<Board.BoardDto>error(Error.RESOURCE_NOT_FOUND).toResponseEntity();
        }
    }

    @Operation(summary = "게시물 삭제", description = "ID로 게시물을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다.")
    })
    @RequiredRole({UserRole.ROLE_SELF, UserRole.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return Response.<Void>ok().toResponseEntity();
    }
}