package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.usuarios.application.estudiante.command.validator.RemoverEstudianteValidator;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteVigenteRule;
import com.arquisoft.usuarios.domain.estudiante.rules.impl.EstudianteVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverEstudianteValidatorImpl implements RemoverEstudianteValidator {

    private final EstudianteVigenteRule estudianteVigenteRule;

    public RemoverEstudianteValidatorImpl() {
        this.estudianteVigenteRule = new EstudianteVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, EstudianteDomain estudiante) {
        estudianteVigenteRule.validar(new ExistenciaEstudiante(usuario, estudiante));
    }
}
