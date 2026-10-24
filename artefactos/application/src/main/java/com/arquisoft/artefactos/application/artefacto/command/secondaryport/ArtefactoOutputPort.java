package com.arquisoft.artefactos.application.artefacto.command.secondaryport;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;

import java.util.List;
import java.util.UUID;

public interface ArtefactoOutputPort {

    void registrarTodos(List<ArtefactoEntity> artefactos);

    void traspasarAsesorDeProyecto(UUID proyectoGrado, UUID asesor);
}
