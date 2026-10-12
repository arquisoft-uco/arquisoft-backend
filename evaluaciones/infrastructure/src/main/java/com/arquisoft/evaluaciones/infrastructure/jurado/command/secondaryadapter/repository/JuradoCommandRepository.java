package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface JuradoCommandRepository extends JpaRepository<JuradoJpaEntity, UUID> {

    @Query("select jurado.id from JuradoJpaEntity jurado "
            + "where jurado.id in :jurados and jurado.eliminadoEn is null")
    Set<UUID> findIdsVigentes(@Param("jurados") Set<UUID> jurados);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE JuradoJpaEntity j
            SET j.identificador = :identificador, j.nombre = :nombre, j.email = :email,
                j.ocurridoEn = :ocurridoEn, j.eliminadoEn = NULL
            WHERE j.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);
}
