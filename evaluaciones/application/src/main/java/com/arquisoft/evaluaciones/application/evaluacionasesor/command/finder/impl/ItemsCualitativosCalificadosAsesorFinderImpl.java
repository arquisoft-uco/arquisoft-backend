package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ItemsCualitativosCalificadosAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsCualitativosCalificadosAsesorFinderImpl implements ItemsCualitativosCalificadosAsesorFinder {

    private final EvaluacionAsesorOutputPort outputPort;

    @Override
    public List<ItemCualitativoCalificado> obtener(UUID evaluacionAsesor) {
        return outputPort.consultarCualitativosCalificados(evaluacionAsesor).stream()
                .map(ResumenEvaluacionMapper::toDomain)
                .toList();
    }
}
