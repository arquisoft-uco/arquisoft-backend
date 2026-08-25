package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UltimoEstadoEvaluacionFichaFinderImpl implements UltimoEstadoEvaluacionFichaFinder {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Override
    public Optional<EstadoEvaluacionFichaDomain> obtener(UUID evaluacionFichaPerfil) {
        return estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionFichaPerfil)
                .map(EstadoEvaluacionFichaMapper::toDomain);
    }
}
