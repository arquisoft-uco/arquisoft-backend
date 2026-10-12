package com.arquisoft.usuarios.application.administrador.command.validator.impl;

import com.arquisoft.usuarios.application.administrador.command.validator.RemoverAdministradorValidator;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.model.AutoeliminacionAdministrador;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadRemocionAdministrador;
import com.arquisoft.usuarios.domain.administrador.model.ExistenciaAdministrador;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorNoAutoeliminacionRule;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorUnicoVigenteRule;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorVigenteRule;
import com.arquisoft.usuarios.domain.administrador.rules.impl.AdministradorNoAutoeliminacionRuleImpl;
import com.arquisoft.usuarios.domain.administrador.rules.impl.AdministradorUnicoVigenteRuleImpl;
import com.arquisoft.usuarios.domain.administrador.rules.impl.AdministradorVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverAdministradorValidatorImpl implements RemoverAdministradorValidator {

    private final AdministradorVigenteRule administradorVigenteRule;
    private final AdministradorNoAutoeliminacionRule administradorNoAutoeliminacionRule;
    private final AdministradorUnicoVigenteRule administradorUnicoVigenteRule;

    public RemoverAdministradorValidatorImpl() {
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.administradorNoAutoeliminacionRule = new AdministradorNoAutoeliminacionRuleImpl();
        this.administradorUnicoVigenteRule = new AdministradorUnicoVigenteRuleImpl();
    }

    @Override
    public void validar(UUID actor, UUID usuario, AdministradorDomain administrador, long administradoresVigentes) {
        administradorVigenteRule.validar(new ExistenciaAdministrador(usuario, administrador));
        administradorNoAutoeliminacionRule.validar(new AutoeliminacionAdministrador(actor, usuario));
        administradorUnicoVigenteRule.validar(
                new DisponibilidadRemocionAdministrador(usuario, administradoresVigentes));
    }
}
