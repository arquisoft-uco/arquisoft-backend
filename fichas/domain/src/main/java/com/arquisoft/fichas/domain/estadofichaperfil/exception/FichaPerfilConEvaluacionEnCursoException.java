package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class FichaPerfilConEvaluacionEnCursoException extends DomainException {

    public FichaPerfilConEvaluacionEnCursoException(UUID fichaPerfil, String tituloProyecto, long enEvaluacion) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoFichaPerfil.EVALUACION_EN_CURSO,
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_EVALUACION_EN_CURSO, tituloProyecto, enEvaluacion),
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_EVALUACION_EN_CURSO_DETALLE, fichaPerfil, enEvaluacion)
        ));
    }
}
