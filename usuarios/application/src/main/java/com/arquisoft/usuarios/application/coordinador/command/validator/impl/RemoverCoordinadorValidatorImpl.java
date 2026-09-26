package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.usuarios.application.coordinador.command.validator.RemoverCoordinadorValidator;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.usuarios.domain.coordinador.rules.CoordinadorVigenteRule;
import com.arquisoft.usuarios.domain.coordinador.rules.impl.CoordinadorVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverCoordinadorValidatorImpl implements RemoverCoordinadorValidator {

    private final CoordinadorVigenteRule coordinadorVigenteRule;

    public RemoverCoordinadorValidatorImpl() {
        this.coordinadorVigenteRule = new CoordinadorVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, CoordinadorDomain coordinador) {
        coordinadorVigenteRule.validar(new ExistenciaCoordinador(usuario, coordinador));
    }
}
