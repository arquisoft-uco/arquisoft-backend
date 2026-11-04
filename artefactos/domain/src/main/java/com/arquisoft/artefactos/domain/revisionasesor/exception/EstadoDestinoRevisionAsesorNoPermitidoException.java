package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class EstadoDestinoRevisionAsesorNoPermitidoException extends DomainException {

    public EstadoDestinoRevisionAsesorNoPermitidoException(UUID revisionAsesor, EstadoRevisionAsesor estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.ESTADO_DESTINO_NO_PERMITIDO,
                Mensajes.formatear(RevisionAsesorKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO, estado.getNombre()),
                Mensajes.formatear(RevisionAsesorKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE,
                        revisionAsesor, estado.getId())
        ));
    }
}
