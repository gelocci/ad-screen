package br.com.locci.adscreen.user.repository;

import br.com.locci.adscreen.user.entity.OrganizationUser;
import br.com.locci.adscreen.user.entity.OrganizationUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, OrganizationUserId> {

    List<OrganizationUser> findByIdUserId(UUID userId);

    List<OrganizationUser> findByIdOrganizationId(UUID organizationId);

    @Query("""
        select ou
        from OrganizationUser ou
        join fetch ou.organization o
        join fetch ou.user u
        where u.email = :email
    """)
    List<OrganizationUser> findByUserEmailFetchOrganization(String email);

    @Query("""
        select ou
        from OrganizationUser ou
        join fetch ou.user u
        where ou.id.organizationId = :organizationId
    """)
    List<OrganizationUser> findByOrganizationIdFetchUser(UUID organizationId);
}
