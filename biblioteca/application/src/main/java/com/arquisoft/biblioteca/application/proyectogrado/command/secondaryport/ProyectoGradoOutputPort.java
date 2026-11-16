package com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport;

import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    Optional<ProyectoGradoEntity> obtenerPorId(UUID id);

    void guardar(ProyectoGradoEntity proyecto);
}
