package dev.oth.gbs.controllers;


import dev.oth.gbs.Response;
import dev.oth.gbs.Error;
import dev.oth.gbs.domain.Board;
import dev.oth.gbs.interfaces.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;


    @Operation(summary = "게시물 생성", description = "새로운 게시물을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Response<Board.BoardDto>> createBoard(@RequestBody Board.BoardDto boardDto) {
        Board.BoardDto createdBoard = boardService.createBoard(boardDto);
        return Response.<Board.BoardDto>ok().withData(createdBoard).toResponseEntity();
    }

    @Operation(summary = "게시물 조회", description = "ID로 게시물을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response<Board.BoardDto>> getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id)
                .map(boardDto -> Response.<Board.BoardDto>ok().withData(boardDto).toResponseEntity())
                .orElse(Response.<Board.BoardDto>error(Error.RESOURCE_NOT_FOUND).toResponseEntity());
    }

    @Operation(summary = "모든 게시물 조회", description = "모든 게시물을 가져옵니다.")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class,
                                    oneOf = { Board.BoardVo.class }))),
            @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class)))
    })
    public ResponseEntity<Response<List<Board.BoardVo>>> getAllBoards() {
        List<Board.BoardVo> boards = boardService.getAllBoards();
        return Response.<List<Board.BoardVo>>ok().withData(boards).toResponseEntity();
    }

    @Operation(summary = "게시물 수정", description = "ID로 게시물을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return Response.<Void>ok().toResponseEntity();
    }
}