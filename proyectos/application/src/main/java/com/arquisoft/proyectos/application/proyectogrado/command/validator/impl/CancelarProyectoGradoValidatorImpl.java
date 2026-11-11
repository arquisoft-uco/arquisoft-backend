package com.arquisoft.proyectos.application.proyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.CancelarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.EstadoProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.PropiedadCoordinadorProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.CoordinadorProyectoGradoPropietarioRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoCancelableRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.CoordinadorProyectoGradoPropietarioRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoCancelableRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CancelarProyectoGradoValidatorImpl implements CancelarProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstadoProyectoGradoExisteRule estadoProyectoGradoExisteRule;
    private final CoordinadorProyectoGradoPropietarioRule coordinadorPropietarioRule;
    private final ProyectoGradoCancelableRule proyectoGradoCancelableRule;

    public CancelarProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estadoProyectoGradoExisteRule = new EstadoProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioRule = new CoordinadorProyectoGradoPropietarioRuleImpl();
        this.proyectoGradoCancelableRule = new ProyectoGradoCancelableRuleImpl();
    }

    @Override
    public void validar(CancelacionProyectoGradoDomain cancelacion, ProyectoGradoDomain proyecto,
                        EstadoProyectoGradoDomain estadoActual) {
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(cancelacion.getProyectoGrado(), !proyecto.esVacio()));
        estadoProyectoGradoExisteRule.validar(
                new ExistenciaEstadoProyectoGrado(cancelacion.getProyectoGrado(), !estadoActual.esVacio()));
        coordinadorPropietarioRule.validar(new PropiedadCoordinadorProyectoGrado(
                cancelacion.getProyectoGrado(), proyecto.getCoordinador(), cancelacion.getCoordinador()));
        proyectoGradoCancelableRule.validar(
                new EstadoActualProyectoGrado(cancelacion.getProyectoGrado(), proyecto.getTituloProyecto(),
                        estadoActual.getEstadoProyecto()));
    }
}
