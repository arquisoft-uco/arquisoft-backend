package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.usuarios.application.asesor.command.validator.RemoverAsesorValidator;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.usuarios.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.usuarios.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverAsesorValidatorImpl implements RemoverAsesorValidator {

    private final AsesorVigenteRule asesorVigenteRule;

    public RemoverAsesorValidatorImpl() {
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, AsesorDomain asesor) {
        asesorVigenteRule.validar(new ExistenciaAsesor(usuario, asesor));
    }
}
