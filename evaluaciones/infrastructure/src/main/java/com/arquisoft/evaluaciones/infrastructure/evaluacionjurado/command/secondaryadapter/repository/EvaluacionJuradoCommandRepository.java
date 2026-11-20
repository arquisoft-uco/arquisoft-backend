package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.CualitativoCalificadoJuradoProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.CuantitativoCalificadoJuradoProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.ContextoRegistroEvaluacionJuradoProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.EstadoEvaluacionJuradoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionJuradoCommandRepository extends JpaRepository<EvaluacionJuradoJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJuradoJpaEntity evaluacionJurado "
            + "set evaluacionJurado.estado = :estado where evaluacionJurado.id = :id")
    int actualizarEstado(@Param("id") UUID id, @Param("estado") String estado);

    // Completa = jurado vigente y todo ítem cualitativo y todo ítem cuantitativo del catálogo tiene su
    // evaluación registrada para esta evaluación de jurado.
    @NativeQuery("""
            SELECT CASE WHEN
                EXISTS (SELECT 1 FROM evaluacion_jurado ej
                        WHERE ej.id = :evaluacionJurado AND ej.eliminado_en IS NULL)
                AND NOT EXISTS (SELECT 1 FROM item_cualitativo_jurado ic
                            WHERE NOT EXISTS (SELECT 1 FROM evaluacion_cualitativa_jurado ecj
                                              WHERE ecj.evaluacion_jurado_id = :evaluacionJurado
                                                AND ecj.item_id = ic.id))
                AND NOT EXISTS (SELECT 1 FROM item_cuantitativo_jurado iq
                                WHERE NOT EXISTS (SELECT 1 FROM evaluacion_cuantitativa_jurado eqj
                                                  WHERE eqj.evaluacion_jurado_id = :evaluacionJurado
                                                    AND eqj.item_id = iq.id))
            THEN TRUE ELSE FALSE END
            """)
    boolean estaCompleta(@Param("evaluacionJurado") UUID evaluacionJurado);

    // FOR UPDATE OF e, ej: el bloqueo cubre el JOIN entre evaluacion y evaluacion_jurado, que @Lock
    // de Spring Data no expresa sobre una unica entidad. El futuro flujo de finalizacion debe
    // adquirir el mismo bloqueo dentro de su propia transaccion.
    @NativeQuery("""
            SELECT ej.id AS id, e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado, e.entregable_id AS entregable,
                   ej.jurado_id AS jurado
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado AND ej.eliminado_en IS NULL
            FOR UPDATE OF e, ej
            """)
    Optional<ContextoRegistroEvaluacionJuradoProjection> buscarContextoBloqueado(
            @Param("evaluacionJurado") UUID evaluacionJurado);

    @Query("select ej.jurado from EvaluacionJuradoJpaEntity ej "
            + "where ej.evaluacion = :evaluacion and ej.jurado in :jurados and ej.eliminadoEn is null")
    Set<UUID> findJuradosAsignados(@Param("evaluacion") UUID evaluacion, @Param("jurados") Set<UUID> jurados);

    @Query("select ej.jurado from EvaluacionJuradoJpaEntity ej "
            + "where ej.evaluacion = :evaluacion and ej.jurado in :jurados "
            + "and ej.estado = 'EVALUADA' and ej.eliminadoEn is null")
    Set<UUID> findJuradosEvaluados(@Param("evaluacion") UUID evaluacion, @Param("jurados") Set<UUID> jurados);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJuradoJpaEntity ej set ej.eliminadoEn = :eliminadoEn "
            + "where ej.evaluacion = :evaluacion and ej.jurado in :jurados and ej.eliminadoEn is null")
    int marcarEliminadosPorEvaluacionYJurados(
            @Param("evaluacion") UUID evaluacion,
            @Param("jurados") Set<UUID> jurados,
            @Param("eliminadoEn") Instant eliminadoEn);

    @Query("select ej from EvaluacionJuradoJpaEntity ej "
            + "where ej.evaluacion = :evaluacion and ej.eliminadoEn is null and ej.notaAcumulada is not null")
    List<EvaluacionJuradoJpaEntity> findConNotaVigentesPorEvaluacion(@Param("evaluacion") UUID evaluacion);

    // Mismo bloqueo que buscarContextoBloqueado: el estado leido es precondicion del cambio de
    // puntaje y no puede pasar a finalizada antes del commit de esa escritura.
    @NativeQuery("""
            SELECT ej.id AS id, ej.jurado_id AS jurado, e.estado_evaluacion_id AS estado
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado AND ej.eliminado_en IS NULL
            FOR UPDATE OF e, ej
            """)
    Optional<EstadoEvaluacionJuradoProjection> buscarEstadoBloqueado(
            @Param("evaluacionJurado") UUID evaluacionJurado);

    @NativeQuery("""
            SELECT (e.estado_evaluacion_id = 'FINALIZADA')
            FROM evaluacion_cuantitativa_jurado ecj
            JOIN evaluacion_jurado ej ON ej.id = ecj.evaluacion_jurado_id
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ecj.id = :evaluacionCuantitativaJurado
            """)
    Optional<Boolean> estaFinalizada(@Param("evaluacionCuantitativaJurado") UUID evaluacionCuantitativaJurado);

    @NativeQuery("""
            SELECT (e.estado_evaluacion_id = 'FINALIZADA')
            FROM observacion_item_jurado o
            JOIN evaluacion_cuantitativa_jurado ecj ON ecj.id = o.evaluacion_cuantitativa_jurado_id
            JOIN evaluacion_jurado ej ON ej.id = ecj.evaluacion_jurado_id
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE o.id = :observacion
            """)
    Optional<Boolean> estaFinalizadaPorObservacion(@Param("observacion") UUID observacion);

    @NativeQuery("""
            SELECT i.peso AS peso, c.nombre AS criterio
            FROM evaluacion_cualitativa_jurado e
            JOIN item_cualitativo_jurado i ON i.id = e.item_id
            JOIN criterio_item_cualitativo_jurado c ON c.id = e.criterio_id
            WHERE e.evaluacion_jurado_id = :evaluacionJurado
            """)
    List<CualitativoCalificadoJuradoProjection> findCualitativosCalificados(@Param("evaluacionJurado") UUID evaluacionJurado);

    @NativeQuery("""
            SELECT e.puntaje AS puntaje, i.valor AS valorMaximo
            FROM evaluacion_cuantitativa_jurado e
            JOIN item_cuantitativo_jurado i ON i.id = e.item_id
            WHERE e.evaluacion_jurado_id = :evaluacionJurado
            """)
    List<CuantitativoCalificadoJuradoProjection> findCuantitativosCalificados(@Param("evaluacionJurado") UUID evaluacionJurado);

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJuradoJpaEntity e set e.resumenCualitativo = :resumenCualitativo, "
            + "e.resumenCuantitativo = :resumenCuantitativo, e.notaAcumulada = :notaAcumulada where e.id = :id")
    int actualizarResumen(
            @Param("id") UUID id,
            @Param("resumenCualitativo") BigDecimal resumenCualitativo,
            @Param("resumenCuantitativo") BigDecimal resumenCuantitativo,
            @Param("notaAcumulada") BigDecimal notaAcumulada);
}
