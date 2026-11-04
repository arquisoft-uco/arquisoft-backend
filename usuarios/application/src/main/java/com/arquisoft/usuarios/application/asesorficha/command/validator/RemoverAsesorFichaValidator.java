package com.arquisoft.usuarios.application.asesorficha.command.validator;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverAsesorFichaValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, AsesorFichaDomain asesorFicha);
}
