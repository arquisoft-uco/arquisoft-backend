package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.OtraObservacionItemJuradoConDescripcionExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtraObservacionItemJuradoConDescripcionExisteFinderImpl
        implements OtraObservacionItemJuradoConDescripcionExisteFinder {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;

    @Override
    public Boolean obtener(ModificacionObservacionItemJuradoDomain modificacion) {
        return observacionItemJuradoOutputPort.existeOtraConDescripcion(
                modificacion.getObservacionItemJurado(), modificacion.getDescripcion());
    }
}
