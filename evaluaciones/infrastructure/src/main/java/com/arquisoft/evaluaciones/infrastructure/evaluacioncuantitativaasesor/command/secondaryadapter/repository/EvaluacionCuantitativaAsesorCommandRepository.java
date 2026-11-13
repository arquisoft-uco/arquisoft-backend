package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.projection.ContextoEvaluacionCuantitativaAsesorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorCommandRepository
        extends JpaRepository<EvaluacionCuantitativaAsesorJpaEntity, UUID> {

    boolean existsByItem(UUID item);

    @Query("select e.item from EvaluacionCuantitativaAsesorJpaEntity e "
            + "where e.evaluacionAsesor = :evaluacionAsesor and e.item in :items")
    Set<UUID> findItemsRegistrados(
            @Param("evaluacionAsesor") UUID evaluacionAsesor, @Param("items") Set<UUID> items);

    @NativeQuery("""
            SELECT eca.id AS id, eca.evaluacion_asesor_id AS evaluacionAsesor,
                   ea.asesor_id AS asesor, e.estado_evaluacion_id AS estado
            FROM evaluacion_cuantitativa_asesor eca
            JOIN evaluacion_asesor ea ON ea.id = eca.evaluacion_asesor_id
            JOIN evaluacion e ON e.id = ea.evaluacion_id
            WHERE eca.id = :evaluacionCuantitativaAsesor
            """)
    Optional<ContextoEvaluacionCuantitativaAsesorProjection> buscarContexto(
            @Param("evaluacionCuantitativaAsesor") UUID evaluacionCuantitativaAsesor);
}
