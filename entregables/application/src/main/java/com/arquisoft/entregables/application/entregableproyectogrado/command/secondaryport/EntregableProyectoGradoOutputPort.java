package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;

import java.util.UUID;

public interface EntregableProyectoGradoOutputPort {

    void registrar(EntregableProyectoGradoEntity entregable);

    int obtenerUltimaVersion(UUID proyectoGrado);
}
