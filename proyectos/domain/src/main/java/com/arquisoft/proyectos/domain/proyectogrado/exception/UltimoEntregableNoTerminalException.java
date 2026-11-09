package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class UltimoEntregableNoTerminalException extends DomainException {

    public UltimoEntregableNoTerminalException(UUID proyectoGrado, String tituloProyecto, int version,
                                               EstadoEntregable estadoEntregable) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.ULTIMO_ENTREGABLE_NO_TERMINAL,
                Mensajes.formatear(ProyectoGradoKey.ERROR_ULTIMO_ENTREGABLE_NO_TERMINAL,
                        version, tituloProyecto, estadoEntregable.getNombre()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_ULTIMO_ENTREGABLE_NO_TERMINAL_DETALLE,
                        proyectoGrado, version, estadoEntregable.getId())
        ));
    }
}
