package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArchivoOrigenArtefactoFinder;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArchivoOrigenArtefactoEntity;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchivoOrigenArtefactoFinderImpl implements ArchivoOrigenArtefactoFinder {

    private final AlmacenamientoArtefactoOutputPort almacenamientoArtefactoOutputPort;

    @Override
    public String obtener(String claveOrigen) {
        return almacenamientoArtefactoOutputPort.obtenerMetadatosOrigen(claveOrigen)
                .map(ArchivoOrigenArtefactoEntity::etag)
                .orElse(UtilTexto.VACIO);
    }
}
