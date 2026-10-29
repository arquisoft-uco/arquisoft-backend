package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosAlmacenamiento;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class VersionArtefactoDomain {

    public static final VersionArtefactoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilNumero.CERO, UtilFecha.VACIO, UtilTexto.VACIO);

    private UUID id;
    private UUID artefacto;
    private int numeroVersion;
    private Instant fechaCreacion;
    private String uriArchivo;

    private VersionArtefactoDomain() {}

    public static VersionArtefactoDomain crear(GeneracionVersionArtefactoDomain generacion, int ultimoNumeroVersion) {
        var version = new VersionArtefactoDomain();
        var result = new ValidationResult();

        version.setId();
        version.setArtefacto(generacion.getArtefacto(), result);
        version.setNumeroVersion(ultimoNumeroVersion, result);
        version.setFechaCreacion();
        version.setUriArchivo();

        result.lanzarSiTieneErrores();
        return version;
    }

    public static VersionArtefactoDomain reconstruir(
            UUID id, UUID artefacto, int numeroVersion, Instant fechaCreacion, String uriArchivo) {
        var version = new VersionArtefactoDomain();
        version.id = id;
        version.artefacto = artefacto;
        version.numeroVersion = numeroVersion;
        version.fechaCreacion = fechaCreacion;
        version.uriArchivo = uriArchivo;
        return version;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.ARTEFACTO_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setNumeroVersion(int ultimoNumeroVersion, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(ultimoNumeroVersion, 0,
                ArtefactosFields.VersionArtefacto.NUMERO_VERSION,
                ArtefactosCodes.VersionArtefacto.NUMERO_VERSION_INVALIDO, result)) {
            return;
        }
        this.numeroVersion = ultimoNumeroVersion + 1;
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarInstanteActual();
    }

    private void setUriArchivo() {
        this.uriArchivo = ArtefactosAlmacenamiento.PREFIJO_VERSIONES + id;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public String getUriArchivo() {
        return uriArchivo;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
