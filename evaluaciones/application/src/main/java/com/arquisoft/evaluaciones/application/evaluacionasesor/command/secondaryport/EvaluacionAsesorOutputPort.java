package com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluacionAsesorOutputPort {

    void registrar(EvaluacionAsesorEntity evaluacionAsesor);

    Optional<EvaluacionAsesorEntity> obtenerPorEvaluacion(UUID evaluacion);

    boolean existenRegistrosPorEvaluacion(UUID evaluacion);

    void actualizarAsesor(UUID evaluacionAsesor, UUID asesor);

    boolean estaCompleta(UUID evaluacionAsesor);

    Optional<ResumenEvaluacionEntity> obtenerResumenPorEvaluacion(UUID evaluacion);

    List<ItemCualitativoCalificadoEntity> consultarCualitativosCalificados(UUID evaluacionAsesor);

    List<ItemCuantitativoCalificadoEntity> consultarCuantitativosCalificados(UUID evaluacionAsesor);

    void actualizarResumen(UUID evaluacionAsesor, ResumenEvaluacionEntity resumen);
}
