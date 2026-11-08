package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoParaPaqueteEntity;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoExcluidoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoIncluidoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactosDelPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.MotivoExclusionPaquete;
import com.arquisoft.shared.util.UtilNumero;

import java.util.List;

public final class ArtefactoParaPaqueteMapper {

    private ArtefactoParaPaqueteMapper() {}

    public static ArtefactosDelPaquete toDomain(List<ArtefactoParaPaqueteEntity> filas) {
        var incluidos = filas.stream()
                .filter(fila -> fila.numeroVersionCerrada() > UtilNumero.CERO)
                .map(ArtefactoParaPaqueteMapper::toIncluido)
                .toList();
        var excluidos = filas.stream()
                .filter(fila -> fila.numeroVersionCerrada() <= UtilNumero.CERO)
                .map(ArtefactoParaPaqueteMapper::toExcluido)
                .toList();
        return new ArtefactosDelPaquete(incluidos, excluidos);
    }

    private static ArtefactoIncluidoPaquete toIncluido(ArtefactoParaPaqueteEntity fila) {
        return new ArtefactoIncluidoPaquete(
                fila.artefacto(),
                fila.versionArtefacto(),
                fila.nombreDocumento(),
                fila.numeroVersionCerrada(),
                fila.ultimaVersion(),
                fila.fechaVersion(),
                fila.uriArchivo());
    }

    private static ArtefactoExcluidoPaquete toExcluido(ArtefactoParaPaqueteEntity fila) {
        var motivo = fila.ultimaVersion() == UtilNumero.CERO
                ? MotivoExclusionPaquete.SIN_VERSIONES
                : MotivoExclusionPaquete.SIN_REVISION_CERRADA;
        return new ArtefactoExcluidoPaquete(fila.artefacto(), fila.nombreDocumento(), motivo, fila.ultimaVersion());
    }
}
