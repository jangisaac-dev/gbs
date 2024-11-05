package dev.oth.gbs.repositories;


import dev.oth.gbs.domain.Ownable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface OwnableRepository<T extends Ownable, ID> extends JpaRepository<T, ID> {

    @Query("SELECT e.ownerId FROM #{#entityName} e WHERE e.id = :id")
    Optional<Long> findOwnerIdById(@Param("id") ID id);
}