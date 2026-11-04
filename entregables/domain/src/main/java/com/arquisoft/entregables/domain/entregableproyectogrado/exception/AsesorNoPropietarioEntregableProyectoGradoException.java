package com.arquisoft.entregables.domain.entregableproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;

import java.util.UUID;

public final class AsesorNoPropietarioEntregableProyectoGradoException extends DomainException {

    public AsesorNoPropietarioEntregableProyectoGradoException(UUID asesor, UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                EntregablesCodes.EntregableProyectoGrado.ASESOR_NO_PROPIETARIO,
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ASESOR_NO_PROPIETARIO),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ASESOR_NO_PROPIETARIO_DETALLE,
                        asesor, entregableProyectoGrado)
        ));
    }
}
