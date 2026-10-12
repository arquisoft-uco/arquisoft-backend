package com.arquisoft.usuarios.application.representantecomite.command.validator.impl;

import com.arquisoft.usuarios.application.representantecomite.command.validator.AgregarRepresentanteComiteValidator;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.model.DisponibilidadRepresentanteComiteUsuario;
import com.arquisoft.usuarios.domain.representantecomite.rules.RepresentanteComiteUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.representantecomite.rules.impl.RepresentanteComiteUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarRepresentanteComiteValidatorImpl implements AgregarRepresentanteComiteValidator {

    private final RepresentanteComiteUsuarioUnicoRule representanteComiteUsuarioUnicoRule;

    public AgregarRepresentanteComiteValidatorImpl() {
        this.representanteComiteUsuarioUnicoRule = new RepresentanteComiteUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, RepresentanteComiteDomain representanteComite) {
        representanteComiteUsuarioUnicoRule.validar(
                new DisponibilidadRepresentanteComiteUsuario(usuario, representanteComite));
    }
}
