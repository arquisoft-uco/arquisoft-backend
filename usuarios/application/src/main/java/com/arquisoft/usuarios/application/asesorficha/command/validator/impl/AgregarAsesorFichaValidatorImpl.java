package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.usuarios.application.asesorficha.command.validator.AgregarAsesorFichaValidator;
import com.arquisoft.usuarios.domain.asesorficha.model.DisponibilidadAsesorFichaUsuario;
import com.arquisoft.usuarios.domain.asesorficha.rules.AsesorFichaUsuarioUnicoRule;
import com.arquisoft.usuarios.domain.asesorficha.rules.impl.AsesorFichaUsuarioUnicoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarAsesorFichaValidatorImpl implements AgregarAsesorFichaValidator {

    private final AsesorFichaUsuarioUnicoRule asesorFichaUsuarioUnicoRule;

    public AgregarAsesorFichaValidatorImpl() {
        this.asesorFichaUsuarioUnicoRule = new AsesorFichaUsuarioUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, boolean yaEsAsesorFicha) {
        asesorFichaUsuarioUnicoRule.validar(new DisponibilidadAsesorFichaUsuario(usuario, yaEsAsesorFicha));
    }
}
