package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ObservacionDomain {

    public static final ObservacionDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO, EstadoObservacion.VACIO);

    private UUID id;
    private UUID revisionAsesor;
    private String descripcion;
    private EstadoObservacion estado;

    private ObservacionDomain() {}

    public static ObservacionDomain reconstruir(
            UUID id, UUID revisionAsesor, String descripcion, EstadoObservacion estado) {
        var observacion = new ObservacionDomain();
        observacion.id = id;
        observacion.revisionAsesor = revisionAsesor;
        observacion.descripcion = descripcion;
        observacion.estado = estado;
        return observacion;
    }

    public static ObservacionDomain crear(UUID revisionAsesor, String descripcion) {
        var observacion = new ObservacionDomain();
        var result = new ValidationResult();

        observacion.setId();
        observacion.setRevisionAsesor(revisionAsesor, result);
        observacion.setDescripcion(descripcion, result);
        observacion.setEstadoInicial();

        result.lanzarSiTieneErrores();
        return observacion;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setRevisionAsesor(UUID revisionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.Observacion.REVISION_ASESOR,
                ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.revisionAsesor = revisionAsesor;
    }

    private void setDescripcion(String descripcion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                ArtefactosFields.Observacion.DESCRIPCION,
                ArtefactosCodes.Observacion.DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada,
                ArtefactosLimits.Observacion.DESCRIPCION_MAX,
                ArtefactosFields.Observacion.DESCRIPCION,
                ArtefactosCodes.Observacion.DESCRIPCION_LONGITUD_EXCEDIDA, result)) {
            return;
        }
        this.descripcion = recortada;
    }

    private void setEstadoInicial() {
        this.estado = EstadoObservacion.NUEVA;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRevisionAsesor() {
        return revisionAsesor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoObservacion getEstado() {
        return estado;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
