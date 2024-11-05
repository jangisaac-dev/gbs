package dev.oth.gbs.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;


/**
 *  BoardEntity 생성
 *  Board.BoardEntity boardEntity = new Board.BoardEntity(1L, "John Doe", 30);
 * -
 *  Convert to DTO
 *  Board.BoardDto boardDto = boardEntity.toDto();
 *  System.out.println("DTO: " + boardDto.getTitle() + ", " + boardDto.getDescription());
 * -
 *  Convert to VO
 *  Board.BoardVo boardVo = boardEntity.toVo();
 *  System.out.println("VO: " + boardVo.getTitle() + ", " + boardVo.getDescription());
 * */

public class Board {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity(name = "tb_board")
    public static class BoardEntity implements Serializable, Ownable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        private Long id;

        @Column(nullable = false)
        private Long ownerId;

        @Column(nullable = false)
        private String title;

        @Column(nullable = false)
        private String description;

        @Column(nullable = false)
        @ColumnDefault("0")
        private Long viewCnt = 0L;

        public BoardDto toDto() {
            return new BoardDto(this.title, this.description);
        }

        public BoardDetailVo toDetailVo() {
            return new BoardDetailVo(this.title, this.description, this.viewCnt);
        }

        public BoardListVo toListVo() {
            return new BoardListVo(this.title, this.viewCnt);
        }

        @Override
        public Long getOwnerId() {
            return ownerId;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    @Schema(description = "게시판 모델")
    public static class BoardBaseObject {
        @Schema(description = "게시물 이름", example = "공지사항")
        protected String title;
    }

    @Schema(description = "게시판 DTO 모델")
    @Getter
    public static class BoardDto extends BoardBaseObject {

        @Schema(description = "게시물 내용", example = "이것은 게시판 내용입니다.")
        protected String description;

        public BoardDto(String title, String description) {
            super(title);
            this.description = description;
        }

        public BoardCreateDao toCreateDao(Long ownerId) {
            return new BoardCreateDao(title, description, ownerId);
        }
    }


    @Getter
    public static class BoardCreateDao extends BoardBaseObject {

        protected String description;
        protected Long ownerId;

        public BoardCreateDao(String title, String description, Long ownerId) {
            super(title);
            this.description = description;
            this.ownerId = ownerId;
        }
    }

    @Schema(description = "게시판 상세 VO 모델")
    @Getter
    public static class BoardDetailVo extends BoardBaseObject {

        @Schema(description = "게시물 내용", example = "이것은 게시판 내용입니다.")
        protected String description;

        @Schema(description = "조회수", example = "0")
        private final Long viewCnt;


        public BoardDetailVo(String title, String description, Long viewCnt) {
            super(title);
            this.description = description;
            this.viewCnt = viewCnt;
        }
    }


    @Schema(description = "게시판 리스트 VO 모델")
    @Getter
    public static class BoardListVo extends BoardBaseObject {

        @Schema(description = "조회수", example = "0")
        private final Long viewCnt;


        public BoardListVo(String title, Long viewCnt) {
            super(title);
            this.viewCnt = viewCnt;
        }
    }

    // Optional mapper methods if you need conversions between different classes
    public static BoardDto convertEntityToDto(BoardEntity entity) {
        return entity.toDto();
    }

    public static BoardDetailVo convertEntityToDetailVo(BoardEntity entity) {
        return entity.toDetailVo();
    }

    public static BoardListVo convertEntityToListVo(BoardEntity entity) {
        return entity.toListVo();
    }
}
