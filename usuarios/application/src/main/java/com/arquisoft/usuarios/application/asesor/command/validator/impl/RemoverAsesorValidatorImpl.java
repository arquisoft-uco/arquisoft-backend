package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.usuarios.application.asesor.command.validator.RemoverAsesorValidator;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.usuarios.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.usuarios.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverAsesorValidatorImpl implements RemoverAsesorValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;

    public RemoverAsesorValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, AsesorDomain asesor) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        asesorVigenteRule.validar(new ExistenciaAsesor(usuario, usuarioDomain.getNombre(), asesor));
    }
}
