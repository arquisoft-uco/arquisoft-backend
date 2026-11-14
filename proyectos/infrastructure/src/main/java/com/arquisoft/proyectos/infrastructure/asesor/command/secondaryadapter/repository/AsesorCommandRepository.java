package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AsesorCommandRepository extends JpaRepository<AsesorJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorJpaEntity a SET a.eliminadoEn = :ocurridoEn, a.ocurridoEn = :ocurridoEn WHERE a.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE AsesorJpaEntity a
            SET a.identificador = :identificador, a.nombre = :nombre, a.email = :email,
                a.ocurridoEn = :ocurridoEn, a.eliminadoEn = NULL
            WHERE a.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);

    @Query("""
            SELECT a FROM AsesorJpaEntity a
            WHERE a.id IN (SELECT ap.asesorId FROM AsesorProyectoGradoJpaEntity ap
                           WHERE ap.proyectoGradoId = :proyectoGrado
                             AND ap.estadoAsesorId = :estadoAsesor
                             AND NOT EXISTS (SELECT 1 FROM AsesorProyectoGradoJpaEntity s
                                             WHERE s.asesorId = ap.asesorId
                                               AND s.proyectoGradoId = ap.proyectoGradoId
                                               AND s.fechaAsignado > ap.fechaAsignado))
            """)
    List<AsesorJpaEntity> buscarPorUltimoEstadoEnProyectoGrado(@Param("proyectoGrado") UUID proyectoGrado,
                                                                @Param("estadoAsesor") String estadoAsesor);
}
