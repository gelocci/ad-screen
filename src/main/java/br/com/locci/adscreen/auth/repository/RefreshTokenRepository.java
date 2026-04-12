package br.com.locci.adscreen.auth.repository;

import br.com.locci.adscreen.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.user.id = :userId")
    void revokeAllByUserId(UUID userId);
}
