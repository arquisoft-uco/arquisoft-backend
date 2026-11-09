package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoParaPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;

import java.util.List;
import java.util.UUID;

public interface PaqueteEntregableOutputPort {

    void registrar(PaqueteEntregableEntity paquete);

    boolean existePorEntregable(UUID entregableProyectoGrado);

    List<ArtefactoParaPaqueteEntity> obtenerArtefactosParaPaquete(UUID proyectoGrado, String estadoCerrada);
}
