package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.usuarios.application.estudiante.command.validator.AgregarEstudianteValidator;
import com.arquisoft.usuarios.domain.estudiante.model.DisponibilidadEstudianteUsuario;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.estudiante.rules.impl.EstudianteUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarEstudianteValidatorImpl implements AgregarEstudianteValidator {

    private final EstudianteUsuarioUnicoRule estudianteUsuarioUnicoRule;

    public AgregarEstudianteValidatorImpl() {
        this.estudianteUsuarioUnicoRule = new EstudianteUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, boolean yaEsEstudiante) {
        estudianteUsuarioUnicoRule.validar(new DisponibilidadEstudianteUsuario(usuario, yaEsEstudiante));
    }
}
