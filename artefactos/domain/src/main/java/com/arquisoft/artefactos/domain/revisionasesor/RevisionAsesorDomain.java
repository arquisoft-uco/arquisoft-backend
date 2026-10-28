package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class RevisionAsesorDomain {

    public static final RevisionAsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), EstadoRevisionAsesor.VACIO);

    private UUID id;
    private UUID versionArtefacto;
    private EstadoRevisionAsesor estado;

    private RevisionAsesorDomain() {}

    public static RevisionAsesorDomain reconstruir(UUID id, UUID versionArtefacto, EstadoRevisionAsesor estado) {
        var revision = new RevisionAsesorDomain();
        revision.id = id;
        revision.versionArtefacto = versionArtefacto;
        revision.estado = estado;
        return revision;
    }

    public boolean tieneEstado(EstadoRevisionAsesor otro) {
        return estado == otro;
    }

    public UUID getId() {
        return id;
    }

    public UUID getVersionArtefacto() {
        return versionArtefacto;
    }

    public EstadoRevisionAsesor getEstado() {
        return estado;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
