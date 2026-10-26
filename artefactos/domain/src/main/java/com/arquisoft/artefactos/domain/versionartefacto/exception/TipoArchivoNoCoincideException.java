package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

public final class TipoArchivoNoCoincideException extends DomainException {

    public TipoArchivoNoCoincideException(String tipoDeclarado, String tipoDetectado) {
        super(
                Mensajes.formatear(VersionArtefactoKey.ERROR_TIPO_NO_COINCIDE, tipoDeclarado, tipoDetectado),
                ArtefactosCodes.VersionArtefacto.TIPO_ARCHIVO_NO_COINCIDE
        );
    }
}
