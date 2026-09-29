package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.usuarios.application.asesor.command.validator.AgregarAsesorValidator;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.model.DisponibilidadAsesorUsuario;
import com.arquisoft.usuarios.domain.asesor.rules.AsesorUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.asesor.rules.impl.AsesorUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarAsesorValidatorImpl implements AgregarAsesorValidator {

    private final AsesorUsuarioUnicoRule asesorUsuarioUnicoRule;

    public AgregarAsesorValidatorImpl() {
        this.asesorUsuarioUnicoRule = new AsesorUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, AsesorDomain asesor) {
        asesorUsuarioUnicoRule.validar(new DisponibilidadAsesorUsuario(usuario, asesor));
    }
}
