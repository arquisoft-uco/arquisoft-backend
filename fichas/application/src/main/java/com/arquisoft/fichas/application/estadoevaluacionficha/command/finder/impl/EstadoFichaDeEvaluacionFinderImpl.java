package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoFichaDeEvaluacionFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper.EstadoFichaDeEvaluacionMapper;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaDeEvaluacionFinderImpl implements EstadoFichaDeEvaluacionFinder {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Override
    public EstadoActualFicha obtener(UUID evaluacionFichaPerfil) {
        return evaluacionFichaPerfilOutputPort.obtenerEstadoFichaActual(evaluacionFichaPerfil)
                .map(EstadoFichaDeEvaluacionMapper::toDomain)
                .orElse(EstadoActualFicha.VACIO);
    }
}
