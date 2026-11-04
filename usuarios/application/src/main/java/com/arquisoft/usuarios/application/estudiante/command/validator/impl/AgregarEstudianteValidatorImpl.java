package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.usuarios.application.estudiante.command.validator.AgregarEstudianteValidator;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.model.DisponibilidadEstudianteUsuario;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.estudiante.rules.impl.EstudianteUsuarioUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class AgregarEstudianteValidatorImpl implements AgregarEstudianteValidator {

    private final EstudianteUsuarioUnicoRule estudianteUsuarioUnicoRule;

    public AgregarEstudianteValidatorImpl() {
        this.estudianteUsuarioUnicoRule = new EstudianteUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, EstudianteDomain estudiante) {
        estudianteUsuarioUnicoRule.validar(
                new DisponibilidadEstudianteUsuario(usuario.getId(), usuario.getNombre(), estudiante));
    }
}
