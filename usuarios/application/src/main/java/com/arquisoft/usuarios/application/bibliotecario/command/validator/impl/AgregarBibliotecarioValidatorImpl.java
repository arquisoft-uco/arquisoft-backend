package com.arquisoft.usuarios.application.bibliotecario.command.validator.impl;

import com.arquisoft.usuarios.application.bibliotecario.command.validator.AgregarBibliotecarioValidator;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.model.DisponibilidadBibliotecarioUsuario;
import com.arquisoft.usuarios.domain.bibliotecario.rules.BibliotecarioUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.bibliotecario.rules.impl.BibliotecarioUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarBibliotecarioValidatorImpl implements AgregarBibliotecarioValidator {

    private final BibliotecarioUsuarioUnicoRule bibliotecarioUsuarioUnicoRule;

    public AgregarBibliotecarioValidatorImpl() {
        this.bibliotecarioUsuarioUnicoRule = new BibliotecarioUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, BibliotecarioDomain bibliotecario) {
        bibliotecarioUsuarioUnicoRule.validar(
                new DisponibilidadBibliotecarioUsuario(usuario, bibliotecario));
    }
}
