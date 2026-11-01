package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.ArtefactoSinAvanceJpaQueryEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.temporal.ChronoUnit;

public final class ArtefactoSinAvanceQueryMapper {

    private ArtefactoSinAvanceQueryMapper() {}

    public static ArtefactoSinAvanceReadModel toReadModel(ArtefactoSinAvanceJpaQueryEntity entity) {
        var fechaUltimaVersion = entity.getFechaUltimaVersion();
        var diasSinAvance = UtilObjeto.noEsNulo(fechaUltimaVersion)
                ? ChronoUnit.DAYS.between(fechaUltimaVersion, UtilFecha.generarInstanteActual())
                : null;

        return new ArtefactoSinAvanceReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getNombreProyecto(),
                entity.getVersionRepositorioArtefactoId(),
                entity.getNombreDocumento(),
                entity.getVersionDocumento(),
                entity.getNombreRepositorio(),
                fechaUltimaVersion,
                diasSinAvance,
                entity.getEstadoUltimaRevision());
    }
}
