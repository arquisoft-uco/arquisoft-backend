package com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionDeRepresentanteExisteFinder;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionDeRepresentanteExisteFinderImpl implements EvaluacionDeRepresentanteExisteFinder {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Override
    public Boolean obtener(EvaluacionFichaPerfilDomain evaluacion) {
        return evaluacionFichaPerfilOutputPort.existePorRepresentanteYFicha(
                evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId());
    }
}
