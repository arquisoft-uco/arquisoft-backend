package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AvanceObservacionesRevisionDomain {

    private UUID revisionAsesor;
    private EstadoObservacion estadoOrigen;
    private EstadoObservacion estadoDestino;

    private AvanceObservacionesRevisionDomain() {}

    public static AvanceObservacionesRevisionDomain crear(UUID revisionAsesor) {
        var avance = new AvanceObservacionesRevisionDomain();
        var result = new ValidationResult();

        avance.setRevisionAsesor(revisionAsesor, result);
        avance.setEstadoOrigen();
        avance.setEstadoDestino();

        result.lanzarSiTieneErrores();
        return avance;
    }

    private void setRevisionAsesor(UUID revisionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.Observacion.REVISION_ASESOR,
                ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.revisionAsesor = revisionAsesor;
    }

    private void setEstadoOrigen() {
        this.estadoOrigen = EstadoObservacion.NUEVA;
    }

    private void setEstadoDestino() {
        this.estadoDestino = EstadoObservacion.EN_PROGRESO;
    }

    public UUID getRevisionAsesor() {
        return revisionAsesor;
    }

    public EstadoObservacion getEstadoOrigen() {
        return estadoOrigen;
    }

    public EstadoObservacion getEstadoDestino() {
        return estadoDestino;
    }
}
