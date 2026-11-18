package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.artefactos.domain.versionartefacto.model.DisponibilidadArchivoVersion;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;

public final class ArchivoVersionPurgadoException extends DomainException {

    public ArchivoVersionPurgadoException(DisponibilidadArchivoVersion disponibilidad) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.ARCHIVO_PURGADO,
                Mensajes.formatear(VersionArtefactoKey.ERROR_ARCHIVO_PURGADO, disponibilidad.nombreProyecto()),
                Mensajes.formatear(VersionArtefactoKey.ERROR_ARCHIVO_PURGADO_DETALLE,
                        disponibilidad.versionArtefacto())
        ));
    }
}
