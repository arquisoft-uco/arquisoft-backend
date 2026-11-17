package com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.OtraObservacionItemAsesorConDescripcionExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtraObservacionItemAsesorConDescripcionExisteFinderImpl
        implements OtraObservacionItemAsesorConDescripcionExisteFinder {

    private final ObservacionItemAsesorOutputPort observacionItemAsesorOutputPort;

    @Override
    public Boolean obtener(ModificacionObservacionItemAsesorDomain modificacion) {
        return observacionItemAsesorOutputPort.existeOtraConDescripcion(
                modificacion.getObservacion(), modificacion.getDescripcion());
    }
}
