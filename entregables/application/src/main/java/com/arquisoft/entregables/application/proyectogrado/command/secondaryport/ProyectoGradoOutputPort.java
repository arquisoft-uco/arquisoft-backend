package com.arquisoft.entregables.application.proyectogrado.command.secondaryport;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void guardar(ProyectoGradoEntity proyecto);

    boolean existePorId(UUID id);
}
