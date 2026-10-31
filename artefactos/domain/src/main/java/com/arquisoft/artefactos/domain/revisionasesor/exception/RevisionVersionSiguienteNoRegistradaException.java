package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionVersionSiguienteNoRegistradaException extends DomainException {

    public RevisionVersionSiguienteNoRegistradaException(UUID observacion, int numeroVersionSiguiente) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.VERSION_SIGUIENTE_NO_REGISTRADA,
                Mensajes.obtener(RevisionAsesorKey.ERROR_VERSION_SIGUIENTE_NO_REGISTRADA),
                Mensajes.formatear(RevisionAsesorKey.ERROR_VERSION_SIGUIENTE_NO_REGISTRADA_DETALLE,
                        observacion, numeroVersionSiguiente)
        ));
    }
}
