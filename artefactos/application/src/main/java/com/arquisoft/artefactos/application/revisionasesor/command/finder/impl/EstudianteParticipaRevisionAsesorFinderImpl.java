package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.EstudianteParticipaRevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteParticipaRevisionAsesorFinderImpl implements EstudianteParticipaRevisionAsesorFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public Boolean obtener(CambioEstadoRevisionAsesorDomain cambio) {
        return revisionAsesorOutputPort.existeParticipacion(cambio.getRevisionAsesor(), cambio.getEstudiante());
    }
}
