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
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverAdministradorValidatorImpl implements RemoverAdministradorValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final AdministradorVigenteRule administradorVigenteRule;
    private final AdministradorNoAutoeliminacionRule administradorNoAutoeliminacionRule;
    private final AdministradorUnicoVigenteRule administradorUnicoVigenteRule;

    public RemoverAdministradorValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.administradorVigenteRule = new AdministradorVigenteRuleImpl();
        this.administradorNoAutoeliminacionRule = new AdministradorNoAutoeliminacionRuleImpl();
        this.administradorUnicoVigenteRule = new AdministradorUnicoVigenteRuleImpl();
    }

    @Override
    public void validar(UUID actor, UUID usuario, UsuarioDomain usuarioDomain, AdministradorDomain administrador,
                        long administradoresVigentes) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        administradorVigenteRule.validar(
                new ExistenciaAdministrador(usuario, usuarioDomain.getNombre(), administrador));
        administradorNoAutoeliminacionRule.validar(new AutoeliminacionAdministrador(actor, usuario));
        administradorUnicoVigenteRule.validar(
                new DisponibilidadRemocionAdministrador(usuario, administradoresVigentes));
    }
}
