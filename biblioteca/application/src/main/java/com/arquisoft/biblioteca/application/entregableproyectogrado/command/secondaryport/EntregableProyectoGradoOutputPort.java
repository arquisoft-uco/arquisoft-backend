package com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoOutputPort {

    Optional<EntregableProyectoGradoEntity> obtenerBloqueadoPorId(UUID id);

    boolean existePorId(UUID id);

    void guardar(EntregableProyectoGradoEntity entregable);
}
