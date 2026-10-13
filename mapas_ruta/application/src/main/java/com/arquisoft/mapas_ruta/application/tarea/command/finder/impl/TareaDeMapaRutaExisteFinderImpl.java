package com.arquisoft.mapas_ruta.application.tarea.command.finder.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaDeMapaRutaExisteFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TareaDeMapaRutaExisteFinderImpl implements TareaDeMapaRutaExisteFinder {

    private final TareaOutputPort tareaOutputPort;

    @Override
    public Boolean obtener(TareaDomain tarea) {
        return tareaOutputPort.existePorMapaRutaYDescripcion(tarea.getMapaRuta(), tarea.getDescripcion());
    }
}
