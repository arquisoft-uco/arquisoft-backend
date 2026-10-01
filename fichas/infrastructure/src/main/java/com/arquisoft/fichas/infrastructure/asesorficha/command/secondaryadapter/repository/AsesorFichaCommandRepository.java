package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorFichaCommandRepository extends JpaRepository<AsesorFichaJpaEntity, UUID> {

    Optional<AsesorFichaJpaEntity> findByIdAndEliminadoEnIsNull(UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorFichaJpaEntity a SET a.eliminadoEn = :ocurridoEn, a.ocurridoEn = :ocurridoEn WHERE a.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE AsesorFichaJpaEntity a
            SET a.identificador = :identificador, a.nombre = :nombre, a.email = :email,
                a.ocurridoEn = :ocurridoEn, a.eliminadoEn = NULL
            WHERE a.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);
}
