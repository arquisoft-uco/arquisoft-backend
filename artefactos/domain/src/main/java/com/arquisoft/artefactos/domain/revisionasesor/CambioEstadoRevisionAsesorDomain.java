package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioEstadoRevisionAsesorDomain {

    private UUID revisionAsesor;
    private EstadoRevisionAsesor estado;
    private UUID estudiante;

    private CambioEstadoRevisionAsesorDomain() {}

    public static CambioEstadoRevisionAsesorDomain crear(UUID revisionAsesor, String estado, UUID estudiante) {
        var cambio = new CambioEstadoRevisionAsesorDomain();
        var result = new ValidationResult();

        cambio.setRevisionAsesor(revisionAsesor, result);
        cambio.setEstado(estado, result);
        cambio.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setRevisionAsesor(UUID revisionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.RevisionAsesor.ID,
                ArtefactosCodes.RevisionAsesor.ID_REQUERIDO, result)) {
            return;
        }
        this.revisionAsesor = revisionAsesor;
    }

    private void setEstado(String estado, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estado);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.RevisionAsesor.ESTADO,
                ArtefactosCodes.RevisionAsesor.ESTADO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoRevisionAsesor.esValido(recortado)) {
            result.agregarError(
                    ArtefactosFields.RevisionAsesor.ESTADO,
                    ArtefactosCodes.RevisionAsesor.ESTADO_INVALIDO,
                    Mensajes.formatear(RevisionAsesorKey.ERROR_ESTADO_INVALIDO, recortado));
            return;
        }
        this.estado = EstadoRevisionAsesor.desde(recortado);
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.RevisionAsesor.ESTUDIANTE,
                ArtefactosCodes.RevisionAsesor.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getRevisionAsesor() {
        return revisionAsesor;
    }

    public EstadoRevisionAsesor getEstado() {
        return estado;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
