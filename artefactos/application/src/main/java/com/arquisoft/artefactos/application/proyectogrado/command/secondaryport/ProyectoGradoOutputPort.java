package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void guardar(ProyectoGradoEntity proyecto);

    boolean existePorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerPorArtefacto(UUID artefacto);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorArtefacto(UUID artefacto);
}
