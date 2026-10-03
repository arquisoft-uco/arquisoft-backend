package com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface EstudianteCommandRepository extends JpaRepository<EstudianteJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EstudianteJpaEntity e SET e.eliminadoEn = :ocurridoEn, e.ocurridoEn = :ocurridoEn WHERE e.id = :id")
    int eliminarLogica(@Param("id") UUID id, @Param("ocurridoEn") Instant ocurridoEn);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE EstudianteJpaEntity e
            SET e.identificador = :identificador, e.nombre = :nombre, e.email = :email,
                e.ocurridoEn = :ocurridoEn, e.eliminadoEn = NULL
            WHERE e.id = :id
            """)
    int reactivar(@Param("id") UUID id, @Param("identificador") String identificador,
                  @Param("nombre") String nombre, @Param("email") String email,
                  @Param("ocurridoEn") Instant ocurridoEn);
}
