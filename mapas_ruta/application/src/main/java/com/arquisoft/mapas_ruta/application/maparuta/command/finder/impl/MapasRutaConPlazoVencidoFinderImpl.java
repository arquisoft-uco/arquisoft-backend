package com.arquisoft.mapas_ruta.application.maparuta.command.finder.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapasRutaConPlazoVencidoFinder;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.mapper.MapaRutaMapper;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MapasRutaConPlazoVencidoFinderImpl implements MapasRutaConPlazoVencidoFinder {

    private final MapaRutaOutputPort mapaRutaOutputPort;

    @Override
    public List<MapaRutaDomain> obtener(LocalDate hoy) {
        return mapaRutaOutputPort.obtenerConPlazoVencido(hoy, EstadoProyectoGrado.EN_PROCESO.getId()).stream()
                .map(MapaRutaMapper::toDomain)
                .toList();
    }
}
