package com.arquisoft.evaluaciones.application.entregable.command.secondaryport;

import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity.EntregableEntity;

import java.util.UUID;

public interface EntregableOutputPort {

    void guardar(EntregableEntity entregable);

    boolean existePorId(UUID id);
}
