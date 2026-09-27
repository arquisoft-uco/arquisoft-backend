package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface RepresentanteComiteCommandRepository
        extends JpaRepository<RepresentanteComiteJpaEntity, UUID> {

    boolean existsByIdAndEliminadoEnIsNull(UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RepresentanteComiteJpaEntity r SET r.eliminadoEn = :ocurridoEn, r.ocurridoEn = :ocurridoEn WHERE r.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE RepresentanteComiteJpaEntity r
            SET r.identificador = :identificador, r.nombre = :nombre, r.email = :email,
                r.ocurridoEn = :ocurridoEn, r.eliminadoEn = NULL
            WHERE r.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);
}
