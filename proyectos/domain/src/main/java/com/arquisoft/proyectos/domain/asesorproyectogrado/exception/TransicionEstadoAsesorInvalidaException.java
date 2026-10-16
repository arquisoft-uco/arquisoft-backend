package com.arquisoft.proyectos.domain.asesorproyectogrado.exception;

import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;

public final class TransicionEstadoAsesorInvalidaException extends DomainException {

    public TransicionEstadoAsesorInvalidaException(EstadoAsesor actual, EstadoAsesor nuevo) {
        super(
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_TRANSICION_INVALIDA, actual.getId(), nuevo.getId()),
                ProyectosCodes.AsesorProyectoGrado.TRANSICION_ESTADO_ASESOR_INVALIDA
        );
    }
}
