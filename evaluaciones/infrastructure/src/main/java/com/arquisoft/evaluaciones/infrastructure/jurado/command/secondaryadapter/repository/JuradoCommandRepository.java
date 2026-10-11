package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface JuradoCommandRepository extends JpaRepository<JuradoJpaEntity, UUID> {

    @Query("select jurado.id from JuradoJpaEntity jurado "
            + "where jurado.id in :jurados and jurado.eliminadoEn is null")
    Set<UUID> findIdsVigentes(@Param("jurados") Set<UUID> jurados);
}
