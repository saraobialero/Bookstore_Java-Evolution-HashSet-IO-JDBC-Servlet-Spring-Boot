package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Role r WHERE r.role = :roleCode")
    boolean existsByRoleCode(@Param("roleCode") RoleCode roleCode);

    @Query("SELECT r FROM Role r WHERE r.role = :roleCode")
    Optional<Role> findByRoleCode(@Param("roleCode") RoleCode roleCode);


}
