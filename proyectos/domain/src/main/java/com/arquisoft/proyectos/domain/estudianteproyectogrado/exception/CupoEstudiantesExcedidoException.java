package com.arquisoft.proyectos.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;

public final class CupoEstudiantesExcedidoException extends DomainException {

    public CupoEstudiantesExcedidoException(int maximo) {
        super(
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_CUPO_EXCEDIDO, maximo),
                ProyectosCodes.EstudianteProyectoGrado.CUPO_EXCEDIDO
        );
    }
}
