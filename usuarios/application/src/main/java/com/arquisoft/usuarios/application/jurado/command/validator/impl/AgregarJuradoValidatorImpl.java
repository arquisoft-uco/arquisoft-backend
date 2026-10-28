package com.arquisoft.usuarios.application.jurado.command.validator.impl;

import com.arquisoft.usuarios.application.jurado.command.validator.AgregarJuradoValidator;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.model.DisponibilidadJuradoUsuario;
import com.arquisoft.usuarios.domain.jurado.rules.JuradoUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.jurado.rules.impl.JuradoUsuarioUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.springframework.stereotype.Component;

@Component
public class AgregarJuradoValidatorImpl implements AgregarJuradoValidator {

    private final JuradoUsuarioUnicoRule juradoUsuarioUnicoRule;

    public AgregarJuradoValidatorImpl() {
        this.juradoUsuarioUnicoRule = new JuradoUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain usuario, JuradoDomain jurado) {
        juradoUsuarioUnicoRule.validar(
                new DisponibilidadJuradoUsuario(usuario.getId(), usuario.getNombre(), jurado));
    }
}
