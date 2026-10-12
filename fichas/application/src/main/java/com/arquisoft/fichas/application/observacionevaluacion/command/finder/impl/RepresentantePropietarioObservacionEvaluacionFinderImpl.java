package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.RepresentantePropietarioObservacionEvaluacionFinder;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepresentantePropietarioObservacionEvaluacionFinderImpl
        implements RepresentantePropietarioObservacionEvaluacionFinder {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Override
    public Boolean obtener(AgregacionObservacionEvaluacionDomain agregacion) {
        return evaluacionFichaPerfilOutputPort.esRepresentantePropietario(
                agregacion.getEvaluacionFichaPerfil(), agregacion.getRepresentanteComite());
    }
}
