package com.arquisoft.fichas.domain.estadoevaluacionficha;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class AgregacionEstadoEvaluacionFichaDomain {

    private EstadoEvaluacionFichaDomain estadoEvaluacionFicha;
    private UUID representanteComite;

    private AgregacionEstadoEvaluacionFichaDomain() {}

    public static AgregacionEstadoEvaluacionFichaDomain crear(
            EstadoEvaluacionFichaDomain estadoEvaluacionFicha, UUID representanteComite) {
        var transaccion = new AgregacionEstadoEvaluacionFichaDomain();
        var result = new ValidationResult();

        transaccion.setEstadoEvaluacionFicha(estadoEvaluacionFicha, result);
        transaccion.setRepresentanteComite(representanteComite, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setEstadoEvaluacionFicha(
            EstadoEvaluacionFichaDomain estadoEvaluacionFicha, ValidationResult result) {
        if (!DomainValidator.noNulo(estadoEvaluacionFicha,
                FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO, result)) {
            return;
        }
        this.estadoEvaluacionFicha = estadoEvaluacionFicha;
    }

    private void setRepresentanteComite(UUID representanteComite, ValidationResult result) {
        if (!DomainValidator.noNulo(representanteComite,
                FichasFields.EstadoEvaluacionFicha.REPRESENTANTE_COMITE,
                FichasCodes.EstadoEvaluacionFicha.REPRESENTANTE_REQUERIDO, result)) {
            return;
        }
        this.representanteComite = representanteComite;
    }

    public EstadoEvaluacionFichaDomain getEstadoEvaluacionFicha() {
        return estadoEvaluacionFicha;
    }

    public UUID getEvaluacionFichaPerfil() {
        return estadoEvaluacionFicha.getEvaluacionFichaPerfilId();
    }

    public EstadoEvaluacion getEstadoEvaluacion() {
        return estadoEvaluacionFicha.getEstadoEvaluacion();
    }

    public UUID getRepresentanteComite() {
        return representanteComite;
    }
}
