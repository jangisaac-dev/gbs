package dev.oth.gbs.repositories;

import dev.oth.gbs.domain.Board;
import dev.oth.gbs.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User.UserEntity, Long> {
    Optional<User.UserEntity> findByEmail(String email);
}
