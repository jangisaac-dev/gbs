package dev.oth.gbs.repositories;

import dev.oth.gbs.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board.BoardEntity, Long> {
    @Modifying
    @Query("update tb_board p set p.viewCnt = p.viewCnt + 1 where p.id = :id")
    void updateViews(@Param("id") Long id);
}