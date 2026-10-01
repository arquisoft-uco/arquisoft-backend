package com.arquisoft.usuarios.application.representantecomite.command.validator.impl;

import com.arquisoft.usuarios.application.representantecomite.command.validator.RemoverRepresentanteComiteValidator;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.model.ExistenciaRepresentanteComite;
import com.arquisoft.usuarios.domain.representantecomite.rules.RepresentanteComiteVigenteRule;
import com.arquisoft.usuarios.domain.representantecomite.rules.impl.RepresentanteComiteVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverRepresentanteComiteValidatorImpl implements RemoverRepresentanteComiteValidator {

    private final RepresentanteComiteVigenteRule representanteComiteVigenteRule;

    public RemoverRepresentanteComiteValidatorImpl() {
        this.representanteComiteVigenteRule = new RepresentanteComiteVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, RepresentanteComiteDomain representanteComite) {
        representanteComiteVigenteRule.validar(new ExistenciaRepresentanteComite(usuario, representanteComite));
    }
}
