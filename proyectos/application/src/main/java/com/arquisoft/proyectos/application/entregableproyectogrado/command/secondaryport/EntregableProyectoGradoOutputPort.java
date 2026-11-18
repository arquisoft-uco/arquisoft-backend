package com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoOutputPort {

    void guardar(EntregableProyectoGradoEntity entregable);

    Optional<EntregableProyectoGradoEntity> obtenerPorId(UUID id);

    Optional<EntregableProyectoGradoEntity> obtenerUltimoPorProyectoGrado(UUID proyectoGrado);
}
