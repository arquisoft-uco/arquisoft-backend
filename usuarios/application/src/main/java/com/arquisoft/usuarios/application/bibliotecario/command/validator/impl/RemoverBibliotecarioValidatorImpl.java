package com.arquisoft.usuarios.application.bibliotecario.command.validator.impl;

import com.arquisoft.usuarios.application.bibliotecario.command.validator.RemoverBibliotecarioValidator;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.model.ExistenciaBibliotecario;
import com.arquisoft.usuarios.domain.bibliotecario.rules.BibliotecarioVigenteRule;
import com.arquisoft.usuarios.domain.bibliotecario.rules.impl.BibliotecarioVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverBibliotecarioValidatorImpl implements RemoverBibliotecarioValidator {

    private final BibliotecarioVigenteRule bibliotecarioVigenteRule;

    public RemoverBibliotecarioValidatorImpl() {
        this.bibliotecarioVigenteRule = new BibliotecarioVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, BibliotecarioDomain bibliotecario) {
        bibliotecarioVigenteRule.validar(new ExistenciaBibliotecario(usuario, bibliotecario));
    }
}
