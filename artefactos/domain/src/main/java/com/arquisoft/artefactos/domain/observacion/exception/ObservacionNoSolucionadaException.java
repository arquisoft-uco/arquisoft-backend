package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

import java.util.UUID;

public final class ObservacionNoSolucionadaException extends DomainException {

    public ObservacionNoSolucionadaException(UUID observacion, EstadoObservacion estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Observacion.NO_SOLUCIONADA,
                Mensajes.obtener(ObservacionKey.ERROR_NO_SOLUCIONADA),
                Mensajes.formatear(ObservacionKey.ERROR_NO_SOLUCIONADA_DETALLE, observacion, estado.getId())
        ));
    }
}
