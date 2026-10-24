package com.arquisoft.artefactos.application.artefacto.command.secondaryport;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;

import java.util.List;

public interface ArtefactoOutputPort {

    void registrarTodos(List<ArtefactoEntity> artefactos);
}
