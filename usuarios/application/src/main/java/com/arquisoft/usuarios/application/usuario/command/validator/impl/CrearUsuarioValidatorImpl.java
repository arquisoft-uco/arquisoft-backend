package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadEmailUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioEmailUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CrearUsuarioValidatorImpl implements CrearUsuarioValidator {

    private final UsuarioEmailUnicoRule usuarioEmailUnicoRule;

    public CrearUsuarioValidatorImpl() {
        this.usuarioEmailUnicoRule = new UsuarioEmailUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, boolean emailYaExiste) {
        usuarioEmailUnicoRule.validar(new DisponibilidadEmailUsuario(usuario.getEmail(), emailYaExiste));
    }
}
