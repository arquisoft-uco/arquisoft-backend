package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.usuarios.application.asesorficha.command.validator.RemoverAsesorFichaValidator;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.model.ExistenciaAsesorFicha;
import com.arquisoft.usuarios.domain.asesorficha.rules.AsesorFichaVigenteRule;
import com.arquisoft.usuarios.domain.asesorficha.rules.impl.AsesorFichaVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverAsesorFichaValidatorImpl implements RemoverAsesorFichaValidator {

    private final AsesorFichaVigenteRule asesorFichaVigenteRule;

    public RemoverAsesorFichaValidatorImpl() {
        this.asesorFichaVigenteRule = new AsesorFichaVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, AsesorFichaDomain asesorFicha) {
        asesorFichaVigenteRule.validar(new ExistenciaAsesorFicha(usuario, asesorFicha));
    }
}
