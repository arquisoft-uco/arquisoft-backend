package com.arquisoft.usuarios.application.asesorficha.command.validator;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;

import java.util.UUID;

public interface AgregarAsesorFichaValidator {

    void validar(UUID usuario, AsesorFichaDomain asesorFicha);
}
