package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearUsuarioValidatorImpl implements CrearUsuarioValidator {

    private final UsuarioEmailUnicoRule usuarioEmailUnicoRule;

    @Override
    public void validar(UsuarioAggregate usuario) {
        usuarioEmailUnicoRule.validar(usuario.getEmail());
    }
}
