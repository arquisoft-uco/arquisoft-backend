package com.arquisoft.mapas_ruta.application.tarea.command.finder.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.DescripcionTareaEnUsoFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.model.DescripcionTareaEnMapaRuta;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescripcionTareaEnUsoFinderImpl implements DescripcionTareaEnUsoFinder {

    private final TareaOutputPort tareaOutputPort;

    @Override
    public Boolean obtener(DescripcionTareaEnMapaRuta entrada) {
        return tareaOutputPort.existeOtraPorMapaRutaYDescripcion(
                entrada.mapaRuta(), entrada.descripcion(), entrada.tarea());
    }
}
