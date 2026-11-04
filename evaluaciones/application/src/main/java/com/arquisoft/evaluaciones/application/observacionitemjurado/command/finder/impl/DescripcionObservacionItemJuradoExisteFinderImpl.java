package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.DescripcionObservacionItemJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.model.CriterioDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescripcionObservacionItemJuradoExisteFinderImpl
        implements DescripcionObservacionItemJuradoExisteFinder {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;

    @Override
    public Boolean obtener(CriterioDescripcionObservacionItemJurado criterio) {
        return observacionItemJuradoOutputPort.existePorEvaluacionYDescripcion(
                criterio.evaluacionCuantitativaJurado(), criterio.descripcion());
    }
}
