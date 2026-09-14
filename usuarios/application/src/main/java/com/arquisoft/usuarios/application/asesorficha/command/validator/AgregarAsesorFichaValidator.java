package com.arquisoft.usuarios.application.asesorficha.command.validator;

import java.util.UUID;

public interface AgregarAsesorFichaValidator {

    void validar(UUID usuario, boolean yaEsAsesorFicha);
}
