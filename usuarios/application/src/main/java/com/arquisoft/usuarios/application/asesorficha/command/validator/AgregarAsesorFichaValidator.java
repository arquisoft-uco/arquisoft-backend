package com.arquisoft.usuarios.application.asesorficha.command.validator;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarAsesorFichaValidator {

    void validar(UsuarioDomain usuario, AsesorFichaDomain asesorFicha);
}
