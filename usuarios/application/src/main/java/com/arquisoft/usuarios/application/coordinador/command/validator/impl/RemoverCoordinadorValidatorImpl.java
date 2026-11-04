package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.usuarios.application.coordinador.command.validator.RemoverCoordinadorValidator;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.usuarios.domain.coordinador.rules.CoordinadorVigenteRule;
import com.arquisoft.usuarios.domain.coordinador.rules.impl.CoordinadorVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverCoordinadorValidatorImpl implements RemoverCoordinadorValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final CoordinadorVigenteRule coordinadorVigenteRule;

    public RemoverCoordinadorValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.coordinadorVigenteRule = new CoordinadorVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, CoordinadorDomain coordinador) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        coordinadorVigenteRule.validar(new ExistenciaCoordinador(usuario, usuarioDomain.getNombre(), coordinador));
    }
}
