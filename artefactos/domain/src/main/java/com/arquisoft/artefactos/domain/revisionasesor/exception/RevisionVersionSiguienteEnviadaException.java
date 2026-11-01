package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionVersionSiguienteEnviadaException extends DomainException {

    public RevisionVersionSiguienteEnviadaException(UUID revisionAsesor, EstadoRevisionAsesor estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.VERSION_SIGUIENTE_ENVIADA,
                Mensajes.obtener(RevisionAsesorKey.ERROR_VERSION_SIGUIENTE_ENVIADA),
                Mensajes.formatear(RevisionAsesorKey.ERROR_VERSION_SIGUIENTE_ENVIADA_DETALLE,
                        revisionAsesor, estado.getId())
        ));
    }
}
