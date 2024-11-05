package dev.oth.gbs.repositories;

import dev.oth.gbs.domain.Board;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends OwnableRepository<Board.BoardEntity, Long> {
    @Modifying
    @Query("update tb_board p set p.viewCnt = p.viewCnt + 1 where p.id = :id")
    void updateViews(@Param("id") Long id);


    // 페이지네이션
    @Query(value = "SELECT p FROM tb_board p ORDER BY p.id limit :limit offset :offset")
    List<Board.BoardEntity> findAllWithPagination(@Param("offset") int offset, @Param("limit") int limit);

}