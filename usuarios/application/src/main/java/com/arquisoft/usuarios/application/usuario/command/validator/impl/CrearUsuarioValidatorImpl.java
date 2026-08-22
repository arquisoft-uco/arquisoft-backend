package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadEmailUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioEmailUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CrearUsuarioValidatorImpl implements CrearUsuarioValidator {

    private final UsuarioEmailUnicoRule usuarioEmailUnicoRule;

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public CrearUsuarioValidatorImpl() {
        this.usuarioEmailUnicoRule = new UsuarioEmailUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, boolean emailYaExiste) {
        usuarioEmailUnicoRule.validar(new DisponibilidadEmailUsuario(usuario.getEmail(), emailYaExiste));
    }
}
