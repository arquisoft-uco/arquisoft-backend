package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.ItemsEvaluacionCualitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.model.ItemsPorEvaluacionAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsEvaluacionCualitativaAsesorRegistradosFinderImpl
        implements ItemsEvaluacionCualitativaAsesorRegistradosFinder {

    private final EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;

    @Override
    public Set<UUID> obtener(ItemsPorEvaluacionAsesor criterio) {
        return evaluacionCualitativaAsesorOutputPort.consultarItemsRegistrados(
                criterio.evaluacionAsesor(), criterio.items());
    }
}
