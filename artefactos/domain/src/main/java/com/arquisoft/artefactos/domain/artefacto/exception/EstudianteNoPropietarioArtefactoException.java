package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class EstudianteNoPropietarioArtefactoException extends DomainException {

    public EstudianteNoPropietarioArtefactoException(UUID estudiante, UUID artefacto) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.ESTUDIANTE_NO_PROPIETARIO,
                Mensajes.obtener(ArtefactoKey.ERROR_ESTUDIANTE_NO_PROPIETARIO),
                Mensajes.formatear(ArtefactoKey.ERROR_ESTUDIANTE_NO_PROPIETARIO_DETALLE, estudiante, artefacto)
        ));
    }
}
