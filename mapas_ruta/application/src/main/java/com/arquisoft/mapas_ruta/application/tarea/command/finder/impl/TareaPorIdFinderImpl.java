package com.arquisoft.mapas_ruta.application.tarea.command.finder.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.mapper.TareaMapper;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TareaPorIdFinderImpl implements TareaPorIdFinder {

    private final TareaOutputPort tareaOutputPort;

    @Override
    public TareaDomain obtener(UUID tarea) {
        return tareaOutputPort.obtenerPorId(tarea)
                .map(TareaMapper::toDomain)
                .orElse(TareaDomain.VACIO);
    }
}
