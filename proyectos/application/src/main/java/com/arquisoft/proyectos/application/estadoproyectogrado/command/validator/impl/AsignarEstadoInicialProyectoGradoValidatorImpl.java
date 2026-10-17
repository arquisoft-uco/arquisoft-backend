package com.arquisoft.proyectos.application.estadoproyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.validator.AsignarEstadoInicialProyectoGradoValidator;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AsignarEstadoInicialProyectoGradoValidatorImpl implements AsignarEstadoInicialProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public AsignarEstadoInicialProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(UUID proyectoGrado, boolean proyectoExiste) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(proyectoGrado, proyectoExiste));
    }
}
