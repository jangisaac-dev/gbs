package dev.oth.gbs.interfaces;

import dev.oth.gbs.domain.Board;

import java.util.List;
import java.util.Optional;

public interface BoardService {
    Board.BoardDto createBoard(Board.BoardDto boardDto);
    Optional<Board.BoardDetailVo> getBoardById(Long id);
    List<Board.BoardListVo> getAllBoards();
    Board.BoardDto updateBoard(Long id, Board.BoardDto boardDto);
    void deleteBoard(Long id);
}