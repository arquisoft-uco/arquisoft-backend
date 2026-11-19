package com.arquisoft.mapas_ruta.application.proyectogrado.command.validator.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.validator.ActualizarEstadoProyectoGradoValidator;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ActualizarEstadoProyectoGradoValidatorImpl implements ActualizarEstadoProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public ActualizarEstadoProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado proyecto) {
        proyectoGradoExisteRule.validar(proyecto);
    }
}
