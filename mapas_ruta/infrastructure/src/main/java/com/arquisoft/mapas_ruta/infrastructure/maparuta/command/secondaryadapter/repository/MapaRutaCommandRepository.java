package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MapaRutaCommandRepository extends JpaRepository<MapaRutaJpaEntity, UUID> {

    boolean existsByProyectoGradoId(UUID proyectoGradoId);

    @Query("""
            SELECT m FROM MapaRutaJpaEntity m, TareaJpaEntity t
            WHERE t.mapaRutaId = m.id AND t.id = :tarea
            """)
    Optional<MapaRutaJpaEntity> buscarPorTarea(@Param("tarea") UUID tarea);

    @Query("""
            SELECT m FROM MapaRutaJpaEntity m
            WHERE m.fechaFin < :hoy
              AND EXISTS (
                  SELECT 1 FROM ProyectoGradoJpaEntity p
                  WHERE p.id = m.proyectoGradoId AND p.estadoProyectoGradoId = :estadoProyecto)
            """)
    List<MapaRutaJpaEntity> buscarConPlazoVencido(
            @Param("hoy") LocalDate hoy, @Param("estadoProyecto") String estadoProyecto);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE MapaRutaJpaEntity m SET m.fechaFin = :fechaFin WHERE m.id = :id")
    int actualizarFechaFin(@Param("id") UUID id, @Param("fechaFin") LocalDate fechaFin);
}
