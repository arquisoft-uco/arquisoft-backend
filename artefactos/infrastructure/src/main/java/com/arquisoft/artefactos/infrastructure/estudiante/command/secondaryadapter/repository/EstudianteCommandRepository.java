package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface EstudianteCommandRepository extends JpaRepository<EstudianteJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EstudianteJpaEntity c SET c.eliminadoEn = :ocurridoEn, c.ocurridoEn = :ocurridoEn WHERE c.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE EstudianteJpaEntity c
            SET c.identificador = :identificador, c.nombre = :nombre, c.email = :email,
                c.ocurridoEn = :ocurridoEn, c.eliminadoEn = NULL
            WHERE c.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);
}
