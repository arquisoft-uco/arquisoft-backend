package com.arquisoft.usuarios.application.administrador.command.validator.impl;

import com.arquisoft.usuarios.application.administrador.command.validator.AgregarAdministradorValidator;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadAdministradorUsuario;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.administrador.rules.impl.AdministradorUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarAdministradorValidatorImpl implements AgregarAdministradorValidator {

    private final AdministradorUsuarioUnicoRule administradorUsuarioUnicoRule;

    public AgregarAdministradorValidatorImpl() {
        this.administradorUsuarioUnicoRule = new AdministradorUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, AdministradorDomain administrador) {
        administradorUsuarioUnicoRule.validar(
                new DisponibilidadAdministradorUsuario(usuario, administrador));
    }
}
