package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.SolicitudEstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoEstadoFinderImpl implements EvaluacionJuradoEstadoFinder {

    private final EvaluacionJuradoOutputPort outputPort;

    @Override
    public EstadoEvaluacionJuradoEntity obtener(SolicitudEstadoEvaluacionJurado solicitud) {
        return outputPort.obtenerEstado(solicitud.evaluacionJurado(), solicitud.jurado());
    }
}
