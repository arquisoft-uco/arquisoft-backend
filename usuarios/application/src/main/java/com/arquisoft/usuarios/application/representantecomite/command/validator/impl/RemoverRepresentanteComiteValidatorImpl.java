package com.arquisoft.usuarios.application.representantecomite.command.validator.impl;

import com.arquisoft.usuarios.application.representantecomite.command.validator.RemoverRepresentanteComiteValidator;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.model.ExistenciaRepresentanteComite;
import com.arquisoft.usuarios.domain.representantecomite.rules.RepresentanteComiteVigenteRule;
import com.arquisoft.usuarios.domain.representantecomite.rules.impl.RepresentanteComiteVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverRepresentanteComiteValidatorImpl implements RemoverRepresentanteComiteValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final RepresentanteComiteVigenteRule representanteComiteVigenteRule;

    public RemoverRepresentanteComiteValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.representanteComiteVigenteRule = new RepresentanteComiteVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, RepresentanteComiteDomain representanteComite) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        representanteComiteVigenteRule.validar(new ExistenciaRepresentanteComite(usuario, usuarioDomain.getNombre(), representanteComite));
    }
}
