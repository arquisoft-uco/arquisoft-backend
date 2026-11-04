package com.arquisoft.mapas_ruta.application.maparuta.command.finder.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaDeProyectoExisteFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MapaRutaDeProyectoExisteFinderImpl implements MapaRutaDeProyectoExisteFinder {

    private final MapaRutaOutputPort mapaRutaOutputPort;

    @Override
    public Boolean obtener(UUID proyectoGrado) {
        return mapaRutaOutputPort.existePorProyectoGrado(proyectoGrado);
    }
}
