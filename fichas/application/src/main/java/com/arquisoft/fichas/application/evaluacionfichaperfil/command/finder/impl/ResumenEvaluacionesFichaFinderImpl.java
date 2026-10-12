package com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.ResumenEvaluacionesFichaFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumenEvaluacionesFichaFinderImpl implements ResumenEvaluacionesFichaFinder {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Override
    public ResumenEvaluacionesFicha obtener(UUID fichaPerfil) {
        var conteos = evaluacionFichaPerfilOutputPort.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil).stream()
                .map(ResumenEvaluacionesFichaFinderImpl::aDominio)
                .toList();
        return new ResumenEvaluacionesFicha(fichaPerfil, conteos);
    }

    private static ConteoEvaluacionesPorEstado aDominio(ConteoEvaluacionesPorEstadoEntity entity) {
        return new ConteoEvaluacionesPorEstado(
                EstadoEvaluacion.desde(entity.estadoEvaluacion()),
                entity.evaluaciones(),
                entity.evaluacionesConObservaciones());
    }
}
