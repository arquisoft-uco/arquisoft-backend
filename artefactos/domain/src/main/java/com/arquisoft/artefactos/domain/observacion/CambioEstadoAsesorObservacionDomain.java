package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioEstadoAsesorObservacionDomain {

    private UUID observacion;
    private EstadoObservacion estado;
    private UUID asesor;

    private CambioEstadoAsesorObservacionDomain() {}

    public static CambioEstadoAsesorObservacionDomain crear(UUID observacion, String estado, UUID asesor) {
        var cambio = new CambioEstadoAsesorObservacionDomain();
        var result = new ValidationResult();

        cambio.setObservacion(observacion, result);
        cambio.setEstado(estado, result);
        cambio.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setObservacion(UUID observacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacion,
                ArtefactosFields.Observacion.ID,
                ArtefactosCodes.Observacion.ID_REQUERIDO, result)) {
            return;
        }
        this.observacion = observacion;
    }

    private void setEstado(String estado, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estado);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.Observacion.ESTADO,
                ArtefactosCodes.Observacion.ESTADO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoObservacion.esValido(recortado)) {
            result.agregarError(
                    ArtefactosFields.Observacion.ESTADO,
                    ArtefactosCodes.Observacion.ESTADO_INVALIDO,
                    Mensajes.formatear(ObservacionKey.ERROR_ESTADO_INVALIDO, recortado));
            return;
        }
        this.estado = EstadoObservacion.desde(recortado);
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Observacion.ASESOR,
                ArtefactosCodes.Observacion.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public UUID getObservacion() {
        return observacion;
    }

    public EstadoObservacion getEstado() {
        return estado;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
