package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemDeEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemDeEvaluacionCuantitativaAsesorFinderImpl implements ItemDeEvaluacionCuantitativaAsesorFinder {

    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;

    @Override
    public UUID obtener(UUID evaluacionCuantitativaAsesor) {
        return evaluacionCuantitativaAsesorOutputPort.obtenerItem(evaluacionCuantitativaAsesor)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
