package com.arquisoft.proyectos.application.proyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.RegistrarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.model.VigenciaCoordinador;
import com.arquisoft.proyectos.domain.coordinador.rules.CoordinadorVigenteRule;
import com.arquisoft.proyectos.domain.coordinador.rules.impl.CoordinadorVigenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrarProyectoGradoValidatorImpl implements RegistrarProyectoGradoValidator {

    private final CoordinadorVigenteRule coordinadorVigenteRule;

    public RegistrarProyectoGradoValidatorImpl() {
        this.coordinadorVigenteRule = new CoordinadorVigenteRuleImpl();
    }

    @Override
    public void validar(UUID coordinador, CoordinadorDomain coordinadorEncontrado) {
        coordinadorVigenteRule.validar(new VigenciaCoordinador(coordinador, coordinadorEncontrado));
    }
}
