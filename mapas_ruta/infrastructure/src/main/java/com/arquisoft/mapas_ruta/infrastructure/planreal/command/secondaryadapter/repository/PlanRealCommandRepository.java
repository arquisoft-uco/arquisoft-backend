package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.entity.PlanRealJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PlanRealCommandRepository extends JpaRepository<PlanRealJpaEntity, UUID> {

    boolean existsByTareaId(UUID tareaId);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE PlanRealJpaEntity pr
            SET pr.tiempoRealDias = :tiempoRealDias, pr.descripcionReal = :descripcionReal
            WHERE pr.tareaId = :tarea
            """)
    int actualizarPorTarea(@Param("tarea") UUID tarea,
                           @Param("tiempoRealDias") int tiempoRealDias,
                           @Param("descripcionReal") String descripcionReal);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE PlanRealJpaEntity pr
            SET pr.tiempoRealDias = :tiempoRealDias, pr.descripcionReal = :descripcionReal
            WHERE pr.id = :planReal
            """)
    int actualizarPorId(@Param("planReal") UUID planReal,
                        @Param("tiempoRealDias") int tiempoRealDias,
                        @Param("descripcionReal") String descripcionReal);
}
