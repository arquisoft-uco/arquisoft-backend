package com.arquisoft.artefactos.application.proyectogrado.command.validator.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoNoCongeladoRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoNoCongeladoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ProyectoGradoNoCongeladoValidatorImpl implements ProyectoGradoNoCongeladoValidator {

    private final ProyectoGradoNoCongeladoRule proyectoGradoNoCongeladoRule;

    public ProyectoGradoNoCongeladoValidatorImpl() {
        this.proyectoGradoNoCongeladoRule = new ProyectoGradoNoCongeladoRuleImpl();
    }

    @Override
    public void validar(ProyectoGradoDomain proyecto) {
        proyectoGradoNoCongeladoRule.validar(new EstadoProyectoGrado(
                proyecto.getId(), proyecto.getNombre(), proyecto.estaCongelado(), proyecto.estaConcluido()));
    }
}
