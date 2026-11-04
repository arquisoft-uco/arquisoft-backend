package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;

public final class CargaPendienteMapper {

    private CargaPendienteMapper() {}

    public static CargaPendienteDomain toDomain(CargaPendienteEntity entity) {
        return CargaPendienteDomain.reconstruir(
                entity.carga(),
                entity.artefacto(),
                entity.solicitante(),
                entity.nombreArchivo(),
                entity.tipoContenido(),
                entity.tamanoBytes(),
                entity.etag());
    }
}
