package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ClavesPaquetesProyectoGradoFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClavesPaquetesProyectoGradoFinderImpl implements ClavesPaquetesProyectoGradoFinder {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Override
    public List<String> obtener(PurgaPaquetesProyectoGradoDomain purga) {
        return paqueteEntregableOutputPort.obtenerClavesPorProyectoGrado(purga.getProyectoGrado());
    }
}
