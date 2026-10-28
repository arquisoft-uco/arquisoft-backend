package com.arquisoft.usuarios.application.administrador.command.validator.impl;

import com.arquisoft.usuarios.application.administrador.command.validator.AgregarAdministradorValidator;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadAdministradorUsuario;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.administrador.rules.impl.AdministradorUsuarioUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class AgregarAdministradorValidatorImpl implements AgregarAdministradorValidator {

    private final AdministradorUsuarioUnicoRule administradorUsuarioUnicoRule;

    public AgregarAdministradorValidatorImpl() {
        this.administradorUsuarioUnicoRule = new AdministradorUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, AdministradorDomain administrador) {
        administradorUsuarioUnicoRule.validar(
                new DisponibilidadAdministradorUsuario(usuario.getId(), usuario.getNombre(), administrador));
    }
}
