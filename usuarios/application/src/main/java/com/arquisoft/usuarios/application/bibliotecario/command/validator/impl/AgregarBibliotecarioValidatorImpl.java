package com.arquisoft.usuarios.application.bibliotecario.command.validator.impl;

import com.arquisoft.usuarios.application.bibliotecario.command.validator.AgregarBibliotecarioValidator;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.model.DisponibilidadBibliotecarioUsuario;
import com.arquisoft.usuarios.domain.bibliotecario.rules.BibliotecarioUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.bibliotecario.rules.impl.BibliotecarioUsuarioUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class AgregarBibliotecarioValidatorImpl implements AgregarBibliotecarioValidator {

    private final BibliotecarioUsuarioUnicoRule bibliotecarioUsuarioUnicoRule;

    public AgregarBibliotecarioValidatorImpl() {
        this.bibliotecarioUsuarioUnicoRule = new BibliotecarioUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, BibliotecarioDomain bibliotecario) {
        bibliotecarioUsuarioUnicoRule.validar(
                new DisponibilidadBibliotecarioUsuario(usuario.getId(), usuario.getNombre(), bibliotecario));
    }
}
