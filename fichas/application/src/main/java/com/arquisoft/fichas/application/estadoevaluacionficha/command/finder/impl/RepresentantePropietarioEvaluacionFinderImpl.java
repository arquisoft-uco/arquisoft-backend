package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepresentantePropietarioEvaluacionFinderImpl implements RepresentantePropietarioEvaluacionFinder {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Override
    public Boolean obtener(AgregacionEstadoEvaluacionFichaDomain agregacion) {
        return evaluacionFichaPerfilOutputPort.esRepresentantePropietario(
                agregacion.getEvaluacionFichaPerfil(), agregacion.getRepresentanteComite());
    }
}
