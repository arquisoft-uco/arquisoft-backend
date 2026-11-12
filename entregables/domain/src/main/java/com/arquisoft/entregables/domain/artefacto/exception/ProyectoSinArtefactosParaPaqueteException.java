package com.arquisoft.entregables.domain.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;

import java.util.UUID;

public final class ProyectoSinArtefactosParaPaqueteException extends DomainException {

    public ProyectoSinArtefactosParaPaqueteException(UUID proyectoGrado, String nombreProyecto) {
        super(BaseError.conDetalle(
                EntregablesCodes.Artefacto.SIN_ARTEFACTOS_PARA_PAQUETE,
                Mensajes.formatear(ArtefactoKey.ERROR_SIN_ARTEFACTOS_PARA_PAQUETE, nombreProyecto),
                Mensajes.formatear(ArtefactoKey.ERROR_SIN_ARTEFACTOS_PARA_PAQUETE_DETALLE, proyectoGrado)
        ));
    }
}
