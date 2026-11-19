package com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AsesorCommandRepository extends JpaRepository<AsesorJpaEntity, UUID> {

    boolean existsByIdAndEliminadoEnIsNull(UUID id);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE AsesorJpaEntity j
            SET j.identificador = :identificador, j.nombre = :nombre, j.email = :email,
                j.ocurridoEn = :ocurridoEn, j.eliminadoEn = NULL
            WHERE j.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorJpaEntity j SET j.eliminadoEn = :ocurridoEn, j.ocurridoEn = :ocurridoEn WHERE j.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @NativeQuery("""
            SELECT a.*
            FROM evaluacion_asesor ea
            JOIN asesor a ON a.id = ea.asesor_id
            WHERE ea.evaluacion_id = :evaluacion
            """)
    Optional<AsesorJpaEntity> findContactoPorEvaluacion(@Param("evaluacion") UUID evaluacion);
}
