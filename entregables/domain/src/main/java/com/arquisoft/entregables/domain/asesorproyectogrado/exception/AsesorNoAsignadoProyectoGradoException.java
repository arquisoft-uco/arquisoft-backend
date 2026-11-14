package com.arquisoft.entregables.domain.asesorproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.AsesorProyectoGradoKey;

import java.util.UUID;

public final class AsesorNoAsignadoProyectoGradoException extends DomainException {

    public AsesorNoAsignadoProyectoGradoException(UUID asesor, UUID proyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.AsesorProyectoGrado.NO_ASIGNADO,
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_NO_ASIGNADO),
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_NO_ASIGNADO_DETALLE, asesor, proyectoGrado)
        ));
    }
}
