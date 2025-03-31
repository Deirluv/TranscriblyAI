package org.itstep.transcriblyai.modules.auth.repositories;

import org.itstep.transcriblyai.modules.auth.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
}
