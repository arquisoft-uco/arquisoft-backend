package com.arquisoft.usuarios.application.bibliotecario.command.validator.impl;

import com.arquisoft.usuarios.application.bibliotecario.command.validator.RemoverBibliotecarioValidator;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.model.ExistenciaBibliotecario;
import com.arquisoft.usuarios.domain.bibliotecario.rules.BibliotecarioVigenteRule;
import com.arquisoft.usuarios.domain.bibliotecario.rules.impl.BibliotecarioVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverBibliotecarioValidatorImpl implements RemoverBibliotecarioValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final BibliotecarioVigenteRule bibliotecarioVigenteRule;

    public RemoverBibliotecarioValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.bibliotecarioVigenteRule = new BibliotecarioVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, BibliotecarioDomain bibliotecario) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        bibliotecarioVigenteRule.validar(new ExistenciaBibliotecario(usuario, usuarioDomain.getNombre(), bibliotecario));
    }
}
