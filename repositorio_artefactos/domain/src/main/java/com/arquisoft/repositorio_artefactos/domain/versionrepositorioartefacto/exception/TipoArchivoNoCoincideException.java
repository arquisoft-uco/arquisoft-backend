package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;

public final class TipoArchivoNoCoincideException extends DomainException {

    public TipoArchivoNoCoincideException(String tipoDeclarado, String tipoDetectado) {
        super(
                Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_TIPO_NO_COINCIDE, tipoDeclarado, tipoDetectado),
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_ARCHIVO_NO_COINCIDE
        );
    }
}
