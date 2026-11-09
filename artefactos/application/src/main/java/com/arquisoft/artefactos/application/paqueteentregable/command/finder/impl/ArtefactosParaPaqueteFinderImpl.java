package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ArtefactosParaPaqueteFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.mapper.ArtefactoParaPaqueteMapper;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactosDelPaquete;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactosParaPaqueteFinderImpl implements ArtefactosParaPaqueteFinder {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Override
    public ArtefactosDelPaquete obtener(UUID proyectoGrado) {
        return ArtefactoParaPaqueteMapper.toDomain(paqueteEntregableOutputPort.obtenerArtefactosParaPaquete(
                proyectoGrado, EstadoRevisionAsesor.CERRADA.getId()));
    }
}
