package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class RevisionAsesorDomain {

    public static final RevisionAsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            EstadoRevisionAsesor.VACIO, UtilFecha.VACIO);

    private UUID id;
    private UUID versionArtefacto;
    private EstadoRevisionAsesor estado;
    private Instant fechaCreacion;

    private RevisionAsesorDomain() {}

    public static RevisionAsesorDomain crear(UUID versionArtefacto) {
        var revision = new RevisionAsesorDomain();
        var result = new ValidationResult();

        revision.setId();
        revision.setVersionArtefacto(versionArtefacto, result);
        revision.setEstadoInicial();
        revision.setFechaCreacion();

        result.lanzarSiTieneErrores();
        return revision;
    }

    public static RevisionAsesorDomain reconstruir(
            UUID id, UUID versionArtefacto, EstadoRevisionAsesor estado, Instant fechaCreacion) {
        var revision = new RevisionAsesorDomain();
        revision.id = id;
        revision.versionArtefacto = versionArtefacto;
        revision.estado = estado;
        revision.fechaCreacion = fechaCreacion;
        return revision;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setVersionArtefacto(UUID versionArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionArtefacto,
                ArtefactosFields.RevisionAsesor.VERSION_ARTEFACTO,
                ArtefactosCodes.RevisionAsesor.VERSION_ARTEFACTO_REQUERIDA, result)) {
            return;
        }
        this.versionArtefacto = versionArtefacto;
    }

    private void setEstadoInicial() {
        this.estado = EstadoRevisionAsesor.PENDIENTE;
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarInstanteActual();
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

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
