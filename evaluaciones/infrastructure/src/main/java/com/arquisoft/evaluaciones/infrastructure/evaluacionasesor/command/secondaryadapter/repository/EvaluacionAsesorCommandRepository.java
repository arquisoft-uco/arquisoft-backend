package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection.CualitativoCalificadoAsesorProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection.CuantitativoCalificadoAsesorProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection.ContextoRegistroEvaluacionAsesorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluacionAsesorCommandRepository extends JpaRepository<EvaluacionAsesorJpaEntity, UUID> {

    // FOR UPDATE OF e, ea: el bloqueo cubre el JOIN entre evaluacion y evaluacion_asesor, que @Lock
    // de Spring Data no expresa sobre una unica entidad. El futuro flujo de finalizacion debe
    // adquirir el mismo bloqueo dentro de su propia transaccion.
    @NativeQuery("""
            SELECT ea.id AS id, e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado, e.entregable_id AS entregable,
                   ea.asesor_id AS asesor
            FROM evaluacion_asesor ea
            JOIN evaluacion e ON e.id = ea.evaluacion_id
            WHERE ea.id = :evaluacionAsesor
            FOR UPDATE OF e, ea
            """)
    Optional<ContextoRegistroEvaluacionAsesorProjection> buscarContextoBloqueado(
            @Param("evaluacionAsesor") UUID evaluacionAsesor);

    @NativeQuery("""
            SELECT ea.id AS id, e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado, e.entregable_id AS entregable,
                   ea.asesor_id AS asesor
            FROM evaluacion_cualitativa_asesor eca
            JOIN evaluacion_asesor ea ON ea.id = eca.evaluacion_asesor_id
            JOIN evaluacion e ON e.id = ea.evaluacion_id
            WHERE eca.id = :evaluacionCualitativa
            FOR UPDATE OF e, ea
            """)
    Optional<ContextoRegistroEvaluacionAsesorProjection> buscarContextoBloqueadoPorEvaluacionCualitativa(
            @Param("evaluacionCualitativa") UUID evaluacionCualitativa);

    Optional<EvaluacionAsesorJpaEntity> findByEvaluacion(UUID evaluacion);

    @Query("select count(ea) > 0 from EvaluacionAsesorJpaEntity ea where ea.evaluacion = :evaluacion "
            + "and (exists (select 1 from EvaluacionCualitativaAsesorJpaEntity c where c.evaluacionAsesor = ea.id) "
            + "or exists (select 1 from EvaluacionCuantitativaAsesorJpaEntity q where q.evaluacionAsesor = ea.id))")
    boolean existenRegistrosPorEvaluacion(@Param("evaluacion") UUID evaluacion);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionAsesorJpaEntity ea set ea.asesor = :asesor where ea.id = :id")
    int actualizarAsesor(@Param("id") UUID id, @Param("asesor") UUID asesor);

    @NativeQuery("""
            SELECT CASE WHEN
                NOT EXISTS (SELECT 1 FROM item_cualitativo_asesor ic
                            WHERE NOT EXISTS (SELECT 1 FROM evaluacion_cualitativa_asesor eca
                                              WHERE eca.evaluacion_asesor_id = :evaluacionAsesor
                                                AND eca.item_id = ic.id))
                AND NOT EXISTS (SELECT 1 FROM item_cuantitativo_asesor iq
                                WHERE NOT EXISTS (SELECT 1 FROM evaluacion_cuantitativa_asesor eqa
                                                  WHERE eqa.evaluacion_asesor_id = :evaluacionAsesor
                                                    AND eqa.item_id = iq.id))
            THEN TRUE ELSE FALSE END
            """)
    boolean estaCompleta(@Param("evaluacionAsesor") UUID evaluacionAsesor);

    @NativeQuery("""
            SELECT i.peso AS peso, c.nombre AS criterio
            FROM evaluacion_cualitativa_asesor e
            JOIN item_cualitativo_asesor i ON i.id = e.item_id
            JOIN criterio_item_cualitativo_asesor c ON c.id = e.criterio_id
            WHERE e.evaluacion_asesor_id = :evaluacionAsesor
            """)
    List<CualitativoCalificadoAsesorProjection> findCualitativosCalificados(@Param("evaluacionAsesor") UUID evaluacionAsesor);

    @NativeQuery("""
            SELECT e.puntaje AS puntaje, i.valor AS valorMaximo
            FROM evaluacion_cuantitativa_asesor e
            JOIN item_cuantitativo_asesor i ON i.id = e.item_id
            WHERE e.evaluacion_asesor_id = :evaluacionAsesor
            """)
    List<CuantitativoCalificadoAsesorProjection> findCuantitativosCalificados(@Param("evaluacionAsesor") UUID evaluacionAsesor);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionAsesorJpaEntity e set e.resumenCualitativo = :resumenCualitativo, "
            + "e.resumenCuantitativo = :resumenCuantitativo, e.notaAcumulada = :notaAcumulada where e.id = :id")
    int actualizarResumen(
            @Param("id") UUID id,
            @Param("resumenCualitativo") BigDecimal resumenCualitativo,
            @Param("resumenCuantitativo") BigDecimal resumenCuantitativo,
            @Param("notaAcumulada") BigDecimal notaAcumulada);
}
