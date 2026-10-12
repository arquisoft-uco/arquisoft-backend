package com.arquisoft.proyectos.domain.asesorproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;

import java.util.UUID;

public final class AsesorYaAsignadoException extends DomainException {

    public AsesorYaAsignadoException(UUID asesor) {
        super(
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_YA_ASIGNADO, asesor),
                ProyectosCodes.AsesorProyectoGrado.ASESOR_YA_ASIGNADO
        );
    }
}
