package com.arquisoft.artefactos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoNoEncontradoException extends DomainException {

    public ProyectoGradoNoEncontradoException(UUID proyectoGrado) {
        super(
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_ENCONTRADO, proyectoGrado),
                ArtefactosCodes.ProyectoGrado.NO_ENCONTRADO
        );
    }
}
