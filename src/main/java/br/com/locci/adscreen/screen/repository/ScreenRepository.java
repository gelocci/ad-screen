package br.com.locci.adscreen.screen.repository;

import br.com.locci.adscreen.screen.entity.Screen;
import br.com.locci.adscreen.screen.entity.ScreenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScreenRepository extends JpaRepository<Screen, UUID> {

    List<Screen> findByOrganization_Id(UUID organizationId);

    List<Screen> findByOrganization_IdAndStatus(UUID organizationId, ScreenStatus status);
}
