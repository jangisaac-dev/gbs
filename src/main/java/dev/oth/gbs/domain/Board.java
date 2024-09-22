package dev.oth.gbs.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


/**
 *  BoardEntity 생성
 *  Board.BoardEntity boardEntity = new Board.BoardEntity(1L, "John Doe", 30);
 *
 *  Convert to DTO
 *  Board.BoardDto boardDto = boardEntity.toDto();
 *  System.out.println("DTO: " + boardDto.getTitle() + ", " + boardDto.getDescription());
 *
 *  Convert to VO
 *  Board.BoardVo boardVo = boardEntity.toVo();
 *  System.out.println("VO: " + boardVo.getTitle() + ", " + boardVo.getDescription());
 * */

public class Board {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity(name = "tb_board")
    public static class BoardEntity implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        private Long id;

        @Column(nullable = false)
        private String title;

        @Column(nullable = false)
        private String description;

        // Convert to DTO (without id)
        public BoardDto toDto() {
            return new BoardDto(this.title, this.description);
        }

        // Convert to VO (without id)
        public BoardVo toVo() {
            return new BoardVo(this.title, this.description);
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    @Schema(description = "게시판 모델")
    public static class BoardBaseObject {
        @Schema(description = "게시물 이름", example = "공지사항")
        protected String title;

        @Schema(description = "게시물 내용", example = "이것은 게시판 내용입니다.")
        protected String description;
    }

    @Schema(description = "게시판 DTO 모델")
    @Getter
    public static class BoardDto extends BoardBaseObject {
        public BoardDto(String title, String description) {
            super(title, description);
        }

        public BoardDto() {
        }
    }

    @Schema(description = "게시판 VO 모델")
    @Getter
    public static class BoardVo extends BoardBaseObject {
        public BoardVo(String title, String description) {
            super(title, description);
        }
        public BoardVo() {

        }

        @Override
        public String toString() {
            return "BoardVo{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    // Optional mapper methods if you need conversions between different classes
    public static BoardDto convertEntityToDto(BoardEntity entity) {
        return entity.toDto();
    }

    public static BoardVo convertEntityToVo(BoardEntity entity) {
        return entity.toVo();
    }
}
