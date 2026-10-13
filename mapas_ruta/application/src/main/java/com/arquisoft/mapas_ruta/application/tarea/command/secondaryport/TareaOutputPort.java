package com.arquisoft.mapas_ruta.application.tarea.command.secondaryport;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;

import java.util.UUID;

public interface TareaOutputPort {

    void registrar(TareaEntity tarea);

    boolean existePorMapaRutaYDescripcion(UUID mapaRuta, String descripcion);
}
