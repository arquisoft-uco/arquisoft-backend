package com.arquisoft.mapas_ruta.domain.asignacionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.key.mapas_ruta.AsignacionAsesorKey;

import java.util.UUID;

public final class AsesorNoAsignadoProyectoGradoException extends DomainException {

    public AsesorNoAsignadoProyectoGradoException(UUID asesor, UUID proyectoGrado, String tituloProyecto) {
        super(BaseError.conDetalle(
                MapasRutaCodes.AsignacionAsesor.NO_ASIGNADO,
                Mensajes.formatear(AsignacionAsesorKey.ERROR_NO_ASIGNADO, tituloProyecto),
                Mensajes.formatear(AsignacionAsesorKey.ERROR_NO_ASIGNADO_DETALLE, asesor, proyectoGrado)
        ));
    }
}
