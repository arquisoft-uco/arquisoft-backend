package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoEnEvaluacionExisteFinder;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoEnEvaluacionExisteFinderImpl implements EstadoEnEvaluacionExisteFinder {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Override
    public Boolean obtener(AgregacionEstadoEvaluacionFichaDomain agregacion) {
        return estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(
                agregacion.getEvaluacionFichaPerfil(), agregacion.getEstadoEvaluacion().getId());
    }
}
