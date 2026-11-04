package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemsEvaluacionCuantitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemsEvaluacionCuantitativaAsesorRegistradosFinderImpl
        implements ItemsEvaluacionCuantitativaAsesorRegistradosFinder {

    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;

    @Override
    public Set<UUID> obtener(ItemsPorEvaluacionCuantitativaAsesor criterio) {
        return evaluacionCuantitativaAsesorOutputPort.consultarItemsRegistrados(
                criterio.evaluacionAsesor(), criterio.items());
    }
}
