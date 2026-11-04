package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

import java.util.UUID;

public final class EstadoDestinoObservacionNoPermitidoException extends DomainException {

    public EstadoDestinoObservacionNoPermitidoException(UUID observacion, EstadoObservacion estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Observacion.ESTADO_DESTINO_NO_PERMITIDO,
                Mensajes.formatear(ObservacionKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO, estado.getNombre()),
                Mensajes.formatear(ObservacionKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE,
                        observacion, estado.getId())
        ));
    }
}
