package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionJuradoOutputPort {

    Optional<EstadoEvaluacionJuradoEntity> obtenerEstadoBloqueado(UUID evaluacionJurado);

    void registrarTodas(List<EvaluacionJuradoEntity> entidades);

    void actualizarEstado(UUID evaluacionJurado, String estado);

    boolean estaCompleta(UUID evaluacionJurado);

    List<ResumenEvaluacionEntity> consultarResumenesPorEvaluacion(UUID evaluacion);

    Set<UUID> consultarJuradosAsignados(UUID evaluacion, Set<UUID> jurados);

    Set<UUID> consultarJuradosEvaluados(UUID evaluacion, Set<UUID> jurados);

    void marcarEliminadosPorEvaluacionYJurados(UUID evaluacion, Set<UUID> jurados, Instant eliminadoEn);

    boolean estaFinalizada(UUID evaluacionCuantitativaJurado);

    boolean estaFinalizadaPorObservacion(UUID observacion);

    List<ItemCualitativoCalificadoEntity> consultarCualitativosCalificados(UUID evaluacionJurado);

    List<ItemCuantitativoCalificadoEntity> consultarCuantitativosCalificados(UUID evaluacionJurado);

    void actualizarResumen(UUID evaluacionJurado, ResumenEvaluacionEntity resumen);
}
