package com.arquisoft.usuarios.application.jurado.command.validator.impl;

import com.arquisoft.usuarios.application.jurado.command.validator.RemoverJuradoValidator;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.model.ExistenciaJurado;
import com.arquisoft.usuarios.domain.jurado.rules.JuradoVigenteRule;
import com.arquisoft.usuarios.domain.jurado.rules.impl.JuradoVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverJuradoValidatorImpl implements RemoverJuradoValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final JuradoVigenteRule juradoVigenteRule;

    public RemoverJuradoValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.juradoVigenteRule = new JuradoVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, JuradoDomain jurado) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        juradoVigenteRule.validar(new ExistenciaJurado(usuario, jurado));
    }
}
