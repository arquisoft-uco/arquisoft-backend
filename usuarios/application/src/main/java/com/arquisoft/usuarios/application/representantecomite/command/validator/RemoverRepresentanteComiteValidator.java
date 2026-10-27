package com.arquisoft.usuarios.application.representantecomite.command.validator;

import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverRepresentanteComiteValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, RepresentanteComiteDomain representanteComite);
}
