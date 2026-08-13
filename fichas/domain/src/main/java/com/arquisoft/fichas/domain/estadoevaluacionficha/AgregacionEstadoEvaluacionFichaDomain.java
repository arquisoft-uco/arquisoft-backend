package com.arquisoft.fichas.domain.estadoevaluacionficha;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class AgregacionEstadoEvaluacionFichaDomain {

    private UUID evaluacionFichaPerfil;
    private EstadoEvaluacion estadoEvaluacion;
    private UUID representanteComite;

    private AgregacionEstadoEvaluacionFichaDomain() {}

    public static AgregacionEstadoEvaluacionFichaDomain crear(
            UUID evaluacionFichaPerfil, String estadoEvaluacion, UUID representanteComite) {
        var transaccion = new AgregacionEstadoEvaluacionFichaDomain();
        var result = new ValidationResult();

        transaccion.setEvaluacionFichaPerfil(evaluacionFichaPerfil, result);
        transaccion.setEstadoEvaluacion(estadoEvaluacion, result);
        transaccion.setRepresentanteComite(representanteComite, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setEvaluacionFichaPerfil(UUID evaluacionFichaPerfil, ValidationResult result) {
        if (!DomainValidator.noNulo(evaluacionFichaPerfil,
                FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA, result)) {
            return;
        }
        this.evaluacionFichaPerfil = evaluacionFichaPerfil;
    }

    private void setEstadoEvaluacion(String estadoEvaluacion, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(estadoEvaluacion,
                FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO, result)) {
            return;
        }
        try {
            this.estadoEvaluacion = EstadoEvaluacion.valueOf(UtilTexto.aplicarTrim(estadoEvaluacion));
        } catch (IllegalArgumentException e) {
            result.agregarError(
                    FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                    FichasCodes.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO,
                    Mensajes.formatear(
                            EstadoEvaluacionFichaKey.ERROR_ESTADO_NO_ENCONTRADO, estadoEvaluacion));
        }
    }

    private void setRepresentanteComite(UUID representanteComite, ValidationResult result) {
        if (!DomainValidator.noNulo(representanteComite,
                FichasFields.EstadoEvaluacionFicha.REPRESENTANTE_COMITE,
                FichasCodes.EstadoEvaluacionFicha.REPRESENTANTE_REQUERIDO, result)) {
            return;
        }
        this.representanteComite = representanteComite;
    }

    public UUID getEvaluacionFichaPerfil() {
        return evaluacionFichaPerfil;
    }

    public EstadoEvaluacion getEstadoEvaluacion() {
        return estadoEvaluacion;
    }

    public UUID getRepresentanteComite() {
        return representanteComite;
    }
}
