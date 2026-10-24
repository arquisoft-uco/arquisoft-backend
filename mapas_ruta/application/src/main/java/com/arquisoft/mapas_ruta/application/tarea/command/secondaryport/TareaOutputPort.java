package com.arquisoft.mapas_ruta.application.tarea.command.secondaryport;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;

import java.util.Optional;
import java.util.UUID;

public interface TareaOutputPort {

    void registrar(TareaEntity tarea);

    Optional<TareaEntity> obtenerPorId(UUID tarea);

    void actualizarEstadoItem(UUID tarea, String estadoItem);

    void actualizarInformacion(UUID tarea, String descripcion, UUID asesor);

    boolean existePorMapaRutaYDescripcion(UUID mapaRuta, String descripcion);

    boolean existeOtraPorMapaRutaYDescripcion(UUID mapaRuta, String descripcion, UUID tarea);
}
