package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface EliminarUsuarioValidator {

    void validar(UUID usuario, UsuarioDomain encontrado, EstudianteDomain estudiante, AsesorDomain asesor,
                 AsesorFichaDomain asesorFicha, CoordinadorDomain coordinador);
}
