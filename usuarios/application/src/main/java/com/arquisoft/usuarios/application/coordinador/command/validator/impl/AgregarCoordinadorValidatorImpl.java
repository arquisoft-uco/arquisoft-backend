package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.usuarios.application.coordinador.command.validator.AgregarCoordinadorValidator;
import com.arquisoft.usuarios.domain.coordinador.model.DisponibilidadCoordinadorUsuario;
import com.arquisoft.usuarios.domain.coordinador.rules.CoordinadorUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.coordinador.rules.impl.CoordinadorUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarCoordinadorValidatorImpl implements AgregarCoordinadorValidator {

    private final CoordinadorUsuarioUnicoRule coordinadorUsuarioUnicoRule;

    public AgregarCoordinadorValidatorImpl() {
        this.coordinadorUsuarioUnicoRule = new CoordinadorUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, boolean yaEsCoordinador) {
        coordinadorUsuarioUnicoRule.validar(new DisponibilidadCoordinadorUsuario(usuario, yaEsCoordinador));
    }
}
