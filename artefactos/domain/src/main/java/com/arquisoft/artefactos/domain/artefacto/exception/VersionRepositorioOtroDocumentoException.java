package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class VersionRepositorioOtroDocumentoException extends DomainException {

    public VersionRepositorioOtroDocumentoException(
            String documento, String nombreRepositorio, UUID versionElegida, UUID versionActual) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_OTRO_DOCUMENTO,
                Mensajes.formatear(ArtefactoKey.ERROR_VERSION_OTRO_DOCUMENTO, documento, nombreRepositorio),
                Mensajes.formatear(ArtefactoKey.ERROR_VERSION_OTRO_DOCUMENTO_DETALLE, versionElegida, versionActual)
        ));
    }
}
