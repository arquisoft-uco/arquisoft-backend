package com.arquisoft.usuarios.application.asesor.command.validator;

import java.util.UUID;

public interface AgregarAsesorValidator {

    void validar(UUID usuario, boolean yaEsAsesor);
}
