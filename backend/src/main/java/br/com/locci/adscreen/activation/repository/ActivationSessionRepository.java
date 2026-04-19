package br.com.locci.adscreen.activation.repository;
import br.com.locci.adscreen.activation.entity.ActivationSession;
import br.com.locci.adscreen.activation.entity.ActivationSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface ActivationSessionRepository extends JpaRepository<ActivationSession, UUID> {
    Optional<ActivationSession> findByActivationCode(UUID activationCode);
    Optional<ActivationSession> findByToken(String token);
    Optional<ActivationSession> findByScreen_IdAndStatus(UUID screenId, ActivationSessionStatus status);
}
