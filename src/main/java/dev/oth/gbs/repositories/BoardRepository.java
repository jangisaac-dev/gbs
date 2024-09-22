package dev.oth.gbs.repositories;

import dev.oth.gbs.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board.BoardEntity, Long> {
    // 기본적인 CRUD 작업은 JpaRepository에서 제공됩니다.
    // 필요한 경우 여기에 커스텀 쿼리 메서드를 추가할 수 있습니다.
}