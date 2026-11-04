package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

import java.util.UUID;

public final class CargaNoCorrespondeException extends DomainException {

    public CargaNoCorrespondeException(UUID carga, String nombreArchivo) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.CARGA_NO_CORRESPONDE,
                Mensajes.formatear(VersionArtefactoKey.ERROR_CARGA_NO_CORRESPONDE, nombreArchivo),
                Mensajes.formatear(VersionArtefactoKey.ERROR_CARGA_NO_CORRESPONDE_DETALLE, carga)
        ));
    }
}
