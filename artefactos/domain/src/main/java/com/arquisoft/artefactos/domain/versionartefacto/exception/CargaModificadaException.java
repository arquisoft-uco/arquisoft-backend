package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class CargaModificadaException extends DomainException {

    public CargaModificadaException(UUID carga) {
        super(
                Mensajes.formatear(VersionArtefactoKey.ERROR_CARGA_MODIFICADA, carga),
                ArtefactosCodes.VersionArtefacto.CARGA_MODIFICADA
        );
    }
}
