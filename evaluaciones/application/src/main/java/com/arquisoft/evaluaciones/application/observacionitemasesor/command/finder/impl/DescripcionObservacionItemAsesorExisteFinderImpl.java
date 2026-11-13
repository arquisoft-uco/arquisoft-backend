package com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.DescripcionObservacionItemAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.model.CriterioDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescripcionObservacionItemAsesorExisteFinderImpl
        implements DescripcionObservacionItemAsesorExisteFinder {

    private final ObservacionItemAsesorOutputPort observacionItemAsesorOutputPort;

    @Override
    public Boolean obtener(CriterioDescripcionObservacionItemAsesor criterio) {
        return observacionItemAsesorOutputPort.existePorEvaluacionYDescripcion(
                criterio.evaluacionCuantitativaAsesor(), criterio.descripcion());
    }
}
