package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

import java.util.UUID;

public final class ObservacionNoEditablePorEstudianteException extends DomainException {

    public ObservacionNoEditablePorEstudianteException(UUID observacion, EstadoObservacion estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Observacion.NO_EDITABLE_ESTUDIANTE,
                Mensajes.obtener(ObservacionKey.ERROR_NO_EDITABLE_ESTUDIANTE),
                Mensajes.formatear(ObservacionKey.ERROR_NO_EDITABLE_ESTUDIANTE_DETALLE, observacion, estado.getId())
        ));
    }
}
