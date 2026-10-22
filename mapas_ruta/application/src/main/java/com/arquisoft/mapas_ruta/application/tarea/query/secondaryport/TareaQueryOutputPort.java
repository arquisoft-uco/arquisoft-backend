package com.arquisoft.mapas_ruta.application.tarea.query.secondaryport;

import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;

import java.util.Optional;
import java.util.UUID;

public interface TareaQueryOutputPort {

    Optional<TareaReadModel> consultarPorId(UUID tarea);
}
