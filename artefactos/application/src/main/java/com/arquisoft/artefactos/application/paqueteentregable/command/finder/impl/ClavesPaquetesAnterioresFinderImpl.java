package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ClavesPaquetesAnterioresFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClavesPaquetesAnterioresFinderImpl implements ClavesPaquetesAnterioresFinder {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Override
    public List<String> obtener(PurgaPaquetesAnterioresDomain purga) {
        return paqueteEntregableOutputPort.obtenerClavesAnteriores(
                purga.getProyectoGrado(), purga.getVersionEntregable());
    }
}
