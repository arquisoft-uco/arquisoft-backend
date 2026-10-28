package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
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
