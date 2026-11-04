package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.usuarios.application.estudiante.command.validator.RemoverEstudianteValidator;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteVigenteRule;
import com.arquisoft.usuarios.domain.estudiante.rules.impl.EstudianteVigenteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverEstudianteValidatorImpl implements RemoverEstudianteValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final EstudianteVigenteRule estudianteVigenteRule;

    public RemoverEstudianteValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.estudianteVigenteRule = new EstudianteVigenteRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain usuarioDomain, EstudianteDomain estudiante) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioDomain));
        estudianteVigenteRule.validar(new ExistenciaEstudiante(usuario, usuarioDomain.getNombre(), estudiante));
    }
}
