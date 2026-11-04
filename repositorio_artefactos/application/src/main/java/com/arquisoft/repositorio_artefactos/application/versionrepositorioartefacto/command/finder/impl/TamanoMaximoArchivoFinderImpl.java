package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TamanoMaximoArchivoFinderImpl implements TamanoMaximoArchivoFinder {

    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;

    @Override
    public Long obtener() {
        return almacenamientoVersionOutputPort.tamanoMaximoBytes();
    }
}
