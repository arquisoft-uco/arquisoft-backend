package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;

public final class CargaPendienteMapper {

    private CargaPendienteMapper() {}

    public static CargaPendienteDomain toDomain(CargaPendienteEntity entity) {
        return CargaPendienteDomain.reconstruir(
                entity.carga(),
                entity.repositorio(),
                entity.solicitante(),
                entity.nombreArchivo(),
                entity.tipoContenido(),
                entity.tamanoBytes(),
                entity.etag());
    }
}
