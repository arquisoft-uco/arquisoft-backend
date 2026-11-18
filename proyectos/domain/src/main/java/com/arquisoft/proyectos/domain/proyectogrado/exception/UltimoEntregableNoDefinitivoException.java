package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class UltimoEntregableNoDefinitivoException extends DomainException {

    public UltimoEntregableNoDefinitivoException(UUID proyectoGrado, String tituloProyecto, int version,
                                               EstadoEntregable estadoEntregable) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.ULTIMO_ENTREGABLE_NO_DEFINITIVO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_ULTIMO_ENTREGABLE_NO_DEFINITIVO,
                        version, tituloProyecto, estadoEntregable.getNombre()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_ULTIMO_ENTREGABLE_NO_DEFINITIVO_DETALLE,
                        proyectoGrado, version, estadoEntregable.getId())
        ));
    }
}
