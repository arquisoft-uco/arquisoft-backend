package com.arquisoft.proyectos.application.proyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.AtrasarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.proyectos.domain.coordinador.rules.CoordinadorExisteRule;
import com.arquisoft.proyectos.domain.coordinador.rules.impl.CoordinadorExisteRuleImpl;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.EstadoProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AtrasarProyectoGradoValidatorImpl implements AtrasarProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstadoProyectoGradoExisteRule estadoProyectoGradoExisteRule;
    private final CoordinadorExisteRule coordinadorExisteRule;

    public AtrasarProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estadoProyectoGradoExisteRule = new EstadoProyectoGradoExisteRuleImpl();
        this.coordinadorExisteRule = new CoordinadorExisteRuleImpl();
    }

    @Override
    public void validar(AtrasoProyectoGradoDomain atraso, ProyectoGradoDomain proyecto,
                        EstadoProyectoGradoDomain estadoActual, CoordinadorDomain coordinador) {
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(atraso.getProyectoGrado(), !proyecto.esVacio()));
        estadoProyectoGradoExisteRule.validar(
                new ExistenciaEstadoProyectoGrado(atraso.getProyectoGrado(), !estadoActual.esVacio()));
        coordinadorExisteRule.validar(
                new ExistenciaCoordinador(proyecto.getCoordinador(), !coordinador.esVacio()));
    }
}
