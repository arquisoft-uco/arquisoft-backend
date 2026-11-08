package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;

import java.util.UUID;

public interface PaqueteEntregableOutputPort {

    void guardar(PaqueteEntregableEntity paquete);

    boolean existePorEntregable(UUID entregableProyectoGrado);
}
