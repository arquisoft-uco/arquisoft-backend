package com.arquisoft.artefactos.domain.asesorproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;

import java.util.UUID;

public final class AsesorNoAsignadoProyectoGradoException extends DomainException {

    public AsesorNoAsignadoProyectoGradoException(UUID asesor, UUID proyectoGrado) {
        super(
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_NO_ASIGNADO, asesor, proyectoGrado),
                ArtefactosCodes.AsesorProyectoGrado.NO_ASIGNADO
        );
    }
}
