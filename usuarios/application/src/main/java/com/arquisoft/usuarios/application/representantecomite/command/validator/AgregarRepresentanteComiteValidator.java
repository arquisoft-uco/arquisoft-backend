package com.arquisoft.usuarios.application.representantecomite.command.validator;

import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarRepresentanteComiteValidator {

    void validar(UsuarioDomain usuario, RepresentanteComiteDomain representanteComite);
}
