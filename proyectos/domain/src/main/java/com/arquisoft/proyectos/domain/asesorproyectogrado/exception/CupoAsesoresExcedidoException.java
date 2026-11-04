package com.arquisoft.proyectos.domain.asesorproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;

public final class CupoAsesoresExcedidoException extends DomainException {

    public CupoAsesoresExcedidoException(int maximo) {
        super(
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_CUPO_EXCEDIDO, maximo),
                ProyectosCodes.AsesorProyectoGrado.CUPO_ASESORES_EXCEDIDO
        );
    }
}
