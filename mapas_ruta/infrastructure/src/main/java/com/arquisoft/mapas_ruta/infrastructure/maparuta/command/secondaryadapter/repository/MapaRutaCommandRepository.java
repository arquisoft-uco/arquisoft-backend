package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MapaRutaCommandRepository extends JpaRepository<MapaRutaJpaEntity, UUID> {

    boolean existsByProyectoGradoId(UUID proyectoGradoId);

    @Query("""
            SELECT m FROM MapaRutaJpaEntity m, TareaJpaEntity t
            WHERE t.mapaRutaId = m.id AND t.id = :tarea
            """)
    Optional<MapaRutaJpaEntity> buscarPorTarea(@Param("tarea") UUID tarea);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE MapaRutaJpaEntity m SET m.fechaFin = :fechaFin WHERE m.id = :id")
    int actualizarFechaFin(@Param("id") UUID id, @Param("fechaFin") LocalDate fechaFin);
}
