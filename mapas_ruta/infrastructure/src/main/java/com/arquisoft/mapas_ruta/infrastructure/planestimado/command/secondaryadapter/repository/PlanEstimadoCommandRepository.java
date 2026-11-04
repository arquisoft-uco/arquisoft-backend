package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.entity.PlanEstimadoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PlanEstimadoCommandRepository extends JpaRepository<PlanEstimadoJpaEntity, UUID> {

    boolean existsByTareaId(UUID tareaId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PlanEstimadoJpaEntity pe SET pe.descripcionEstimado = :descripcionEstimado WHERE pe.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcionEstimado") String descripcionEstimado);

    @Query("""
            SELECT COALESCE(SUM(pe.tiempoEstimadoDias), 0L)
            FROM PlanEstimadoJpaEntity pe, TareaJpaEntity t
            WHERE t.id = pe.tareaId
              AND t.mapaRutaId = (SELECT t2.mapaRutaId FROM TareaJpaEntity t2 WHERE t2.id = :tarea)
            """)
    long sumarDiasEstimadosPorMapaRutaDeTarea(@Param("tarea") UUID tarea);
}
