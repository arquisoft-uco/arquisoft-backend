package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearUsuarioValidator {

    private final UsuarioEmailUnicoRule usuarioEmailUnicoRule;

    public void validar(UsuarioAggregate usuario) {
        usuarioEmailUnicoRule.validar(usuario.getEmail());
    }
}
