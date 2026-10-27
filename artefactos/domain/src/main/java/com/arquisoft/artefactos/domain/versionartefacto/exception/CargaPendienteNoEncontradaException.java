package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class CargaPendienteNoEncontradaException extends DomainException {

    public CargaPendienteNoEncontradaException(UUID carga) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.CARGA_NO_ENCONTRADA,
                Mensajes.obtener(VersionArtefactoKey.ERROR_CARGA_NO_ENCONTRADA),
                Mensajes.formatear(VersionArtefactoKey.ERROR_CARGA_NO_ENCONTRADA_DETALLE, carga)
        ));
    }
}
