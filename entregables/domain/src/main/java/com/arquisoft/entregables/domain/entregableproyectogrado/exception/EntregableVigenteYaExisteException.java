package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class EntregableVigenteYaExisteException extends DomainException {

    public EntregableVigenteYaExisteException(UUID proyectoGrado, String nombreProyecto) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.VIGENTE_YA_EXISTE,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_VIGENTE_YA_EXISTE, nombreProyecto),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_VIGENTE_YA_EXISTE_DETALLE, proyectoGrado)
        ));
    }
}
