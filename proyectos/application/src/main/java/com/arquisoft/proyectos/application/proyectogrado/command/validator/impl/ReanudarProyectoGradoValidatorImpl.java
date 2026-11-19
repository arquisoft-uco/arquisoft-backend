package com.arquisoft.proyectos.application.proyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.ReanudarProyectoGradoValidator;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.proyectos.domain.coordinador.rules.CoordinadorExisteRule;
import com.arquisoft.proyectos.domain.coordinador.rules.impl.CoordinadorExisteRuleImpl;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.EstadoProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ReanudarProyectoGradoValidatorImpl implements ReanudarProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstadoProyectoGradoExisteRule estadoProyectoGradoExisteRule;
    private final CoordinadorExisteRule coordinadorExisteRule;

    public ReanudarProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estadoProyectoGradoExisteRule = new EstadoProyectoGradoExisteRuleImpl();
        this.coordinadorExisteRule = new CoordinadorExisteRuleImpl();
    }

    @Override
    public void validar(ReanudacionProyectoGradoDomain reanudacion, ProyectoGradoDomain proyecto,
                        EstadoProyectoGradoDomain estadoActual, CoordinadorDomain coordinador) {
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(reanudacion.getProyectoGrado(), !proyecto.esVacio()));
        estadoProyectoGradoExisteRule.validar(
                new ExistenciaEstadoProyectoGrado(reanudacion.getProyectoGrado(), !estadoActual.esVacio()));
        coordinadorExisteRule.validar(
                new ExistenciaCoordinador(proyecto.getCoordinador(), !coordinador.esVacio()));
    }
}
