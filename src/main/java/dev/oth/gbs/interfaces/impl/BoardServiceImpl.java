package dev.oth.gbs.interfaces.impl;

import dev.oth.gbs.domain.Board;
import dev.oth.gbs.interfaces.BoardService;
import dev.oth.gbs.repositories.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public Board.BoardDto createBoard(Board.BoardDto boardDto) {
        Board.BoardEntity boardEntity = new Board.BoardEntity();
        boardEntity.setTitle(boardDto.getTitle());
        boardEntity.setDescription(boardDto.getDescription());
        Board.BoardEntity savedEntity = boardRepository.save(boardEntity);
        return savedEntity.toDto();
    }

    @Override
    @Transactional
    public Optional<Board.BoardDetailVo> getBoardById(Long id) {
        Optional<Board.BoardEntity> result = boardRepository.findById(id);
        result.ifPresent(boardEntity -> updateViews(boardEntity.getId()));
        return result.map(Board.BoardEntity::toDetailVo);
    }

    @Transactional
    public void updateViews(Long id) {
        boardRepository.updateViews(id);
    }

    @Override
    public List<Board.BoardListVo> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(Board.BoardEntity::toListVo)
                .collect(Collectors.toList());
    }

    @Override
    public Board.BoardDto updateBoard(Long id, Board.BoardDto boardDto) {
        Optional<Board.BoardEntity> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isPresent()) {
            Board.BoardEntity boardEntity = optionalBoard.get();
            boardEntity.setTitle(boardDto.getTitle());
            boardEntity.setDescription(boardDto.getDescription());
            Board.BoardEntity updatedEntity = boardRepository.save(boardEntity);
            return updatedEntity.toDto();
        }
        throw new RuntimeException("Board not found with id: " + id);
    }

    @Override
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}