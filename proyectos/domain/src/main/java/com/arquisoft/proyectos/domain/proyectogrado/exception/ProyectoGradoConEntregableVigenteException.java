package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoConEntregableVigenteException extends DomainException {

    public ProyectoGradoConEntregableVigenteException(UUID proyectoGrado, String tituloProyecto, int version,
                                                      EstadoEntregable estadoEntregable) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.CON_ENTREGABLE_VIGENTE,
                Mensajes.formatear(ProyectoGradoKey.ERROR_CON_ENTREGABLE_VIGENTE,
                        tituloProyecto, version, estadoEntregable.getNombre().toLowerCase()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_CON_ENTREGABLE_VIGENTE_DETALLE,
                        proyectoGrado, version, estadoEntregable.getId())
        ));
    }
}
