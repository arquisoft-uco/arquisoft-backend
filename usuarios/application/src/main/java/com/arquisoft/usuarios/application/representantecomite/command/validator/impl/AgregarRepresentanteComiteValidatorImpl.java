package com.arquisoft.usuarios.application.representantecomite.command.validator.impl;

import com.arquisoft.usuarios.application.representantecomite.command.validator.AgregarRepresentanteComiteValidator;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.model.DisponibilidadRepresentanteComiteUsuario;
import com.arquisoft.usuarios.domain.representantecomite.rules.RepresentanteComiteUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.representantecomite.rules.impl.RepresentanteComiteUsuarioUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class AgregarRepresentanteComiteValidatorImpl implements AgregarRepresentanteComiteValidator {

    private final RepresentanteComiteUsuarioUnicoRule representanteComiteUsuarioUnicoRule;

    public AgregarRepresentanteComiteValidatorImpl() {
        this.representanteComiteUsuarioUnicoRule = new RepresentanteComiteUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, RepresentanteComiteDomain representanteComite) {
        representanteComiteUsuarioUnicoRule.validar(
                new DisponibilidadRepresentanteComiteUsuario(usuario.getId(), usuario.getNombre(), representanteComite));
    }
}
