package com.arquisoft.mapas_ruta.application.maparuta.command.finder.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.mapper.MapaRutaMapper;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MapaRutaPorIdFinderImpl implements MapaRutaPorIdFinder {

    private final MapaRutaOutputPort mapaRutaOutputPort;

    @Override
    public MapaRutaDomain obtener(UUID mapaRuta) {
        return mapaRutaOutputPort.obtenerPorId(mapaRuta)
                .map(MapaRutaMapper::toDomain)
                .orElse(MapaRutaDomain.VACIO);
    }
}
