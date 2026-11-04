package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class ObservacionesVersionAnteriorSinCerrarException extends DomainException {

    public ObservacionesVersionAnteriorSinCerrarException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.OBSERVACIONES_VERSION_ANTERIOR_SIN_CERRAR,
                Mensajes.obtener(RevisionAsesorKey.ERROR_OBSERVACIONES_VERSION_ANTERIOR_SIN_CERRAR),
                Mensajes.formatear(
                        RevisionAsesorKey.ERROR_OBSERVACIONES_VERSION_ANTERIOR_SIN_CERRAR_DETALLE, revisionAsesor)
        ));
    }
}
