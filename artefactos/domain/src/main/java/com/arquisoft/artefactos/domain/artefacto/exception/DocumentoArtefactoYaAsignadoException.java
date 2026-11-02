package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class DocumentoArtefactoYaAsignadoException extends DomainException {

    public DocumentoArtefactoYaAsignadoException(String nombreDocumento, String nombreRepositorio, UUID repositorio) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.DOCUMENTO_YA_ASIGNADO,
                Mensajes.formatear(ArtefactoKey.ERROR_DOCUMENTO_YA_ASIGNADO, nombreDocumento, nombreRepositorio),
                Mensajes.formatear(ArtefactoKey.ERROR_DOCUMENTO_YA_ASIGNADO_DETALLE, nombreDocumento, repositorio)
        ));
    }
}
