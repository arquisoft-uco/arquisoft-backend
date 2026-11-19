package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ItemsCuantitativosCalificadosAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsCuantitativosCalificadosAsesorFinderImpl implements ItemsCuantitativosCalificadosAsesorFinder {

    private final EvaluacionAsesorOutputPort outputPort;

    @Override
    public List<ItemCuantitativoCalificado> obtener(UUID evaluacionAsesor) {
        return outputPort.consultarCuantitativosCalificados(evaluacionAsesor).stream()
                .map(ResumenEvaluacionMapper::toDomain)
                .toList();
    }
}
