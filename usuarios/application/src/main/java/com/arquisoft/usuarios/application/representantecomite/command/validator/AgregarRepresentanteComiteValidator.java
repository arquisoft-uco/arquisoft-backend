package com.arquisoft.usuarios.application.representantecomite.command.validator;

import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;

import java.util.UUID;

public interface AgregarRepresentanteComiteValidator {

    void validar(UUID usuario, RepresentanteComiteDomain representanteComite);
}
