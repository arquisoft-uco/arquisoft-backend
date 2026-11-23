package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.AvanceEvaluacionProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.ContextoAsignacionEvaluacionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface EvaluacionCommandRepository extends JpaRepository<EvaluacionJpaEntity, UUID> {

    boolean existsByEntregable(UUID entregable);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJpaEntity evaluacion "
            + "set evaluacion.estadoEvaluacion = :estado where evaluacion.id = :id")
    int actualizarEstado(@Param("id") UUID id, @Param("estado") String estado);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJpaEntity evaluacion set evaluacion.notaAsesor = :notaAsesor, "
            + "evaluacion.notaJurados = :notaJurados, evaluacion.pesoAsesor = :pesoAsesor, "
            + "evaluacion.pesoJurados = :pesoJurados, evaluacion.cantidadJurados = :cantidadJurados "
            + "where evaluacion.id = :id")
    int actualizarNotas(
            @Param("id") UUID id,
            @Param("notaAsesor") BigDecimal notaAsesor,
            @Param("notaJurados") BigDecimal notaJurados,
            @Param("pesoAsesor") BigDecimal pesoAsesor,
            @Param("pesoJurados") BigDecimal pesoJurados,
            @Param("cantidadJurados") int cantidadJurados);

    @NativeQuery("""
            SELECT e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado,
                   en.id AS entregable,
                   en.proyecto AS proyecto,
                   en.version_entregable AS version,
                   (SELECT COUNT(*) FROM evaluacion_jurado ej
                    WHERE ej.evaluacion_id = e.id AND ej.eliminado_en IS NULL) AS juradosVigentes,
                   (SELECT COUNT(*) FROM evaluacion_jurado ej
                    WHERE ej.evaluacion_id = e.id AND ej.eliminado_en IS NULL
                      AND ej.estado_evaluacion_asignada_jurado_id = 'EVALUADA') AS juradosEvaluados,
                   EXISTS (SELECT 1 FROM evaluacion_asesor ea
                           WHERE ea.evaluacion_id = e.id AND ea.nota_acumulada IS NOT NULL) AS asesorCompleto
            FROM evaluacion e
            JOIN entregable en ON en.id = e.entregable_id
            WHERE e.id = :evaluacion
            """)
    Optional<AvanceEvaluacionProjection> buscarAvance(@Param("evaluacion") UUID evaluacion);

    @NativeQuery("""
            SELECT CASE WHEN
                EXISTS (SELECT 1 FROM observacion_item_asesor oa
                        JOIN evaluacion_cuantitativa_asesor eqa ON eqa.id = oa.evaluacion_cuantitativa_asesor_id
                        JOIN evaluacion_asesor ea ON ea.id = eqa.evaluacion_asesor_id
                        WHERE ea.evaluacion_id = :evaluacion)
                OR EXISTS (SELECT 1 FROM observacion_item_jurado oj
                           JOIN evaluacion_cuantitativa_jurado eqj ON eqj.id = oj.evaluacion_cuantitativa_jurado_id
                           JOIN evaluacion_jurado ej ON ej.id = eqj.evaluacion_jurado_id
                           WHERE ej.evaluacion_id = :evaluacion AND ej.eliminado_en IS NULL)
            THEN TRUE ELSE FALSE END
            """)
    boolean existenObservaciones(@Param("evaluacion") UUID evaluacion);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJpaEntity evaluacion set evaluacion.estadoEvaluacion = :estado, "
            + "evaluacion.notaFinal = :notaFinal, evaluacion.resultadoEvaluacion = :resultado "
            + "where evaluacion.id = :id")
    int finalizar(
            @Param("id") UUID id,
            @Param("estado") String estado,
            @Param("notaFinal") BigDecimal notaFinal,
            @Param("resultado") String resultado);

    // El bloqueo debe ser el mismo que adquirira el futuro flujo de finalizacion.
    @NativeQuery("""
            SELECT e.id AS evaluacion, e.estado_evaluacion_id AS estado
            FROM evaluacion e
            WHERE e.id = :evaluacion
            FOR UPDATE
            """)
    Optional<ContextoAsignacionEvaluacionProjection> buscarContextoAsignacionBloqueado(
            @Param("evaluacion") UUID evaluacion);
}
