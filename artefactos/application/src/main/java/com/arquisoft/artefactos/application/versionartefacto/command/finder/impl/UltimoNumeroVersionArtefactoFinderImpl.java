package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UltimoNumeroVersionArtefactoFinderImpl implements UltimoNumeroVersionArtefactoFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public Integer obtener(UUID artefacto) {
        return versionArtefactoOutputPort.ultimoNumeroPorArtefacto(artefacto);
    }
}
