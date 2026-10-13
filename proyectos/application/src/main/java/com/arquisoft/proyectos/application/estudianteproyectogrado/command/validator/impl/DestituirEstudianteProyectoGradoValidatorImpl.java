package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.DestituirEstudianteProyectoGradoValidator;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.EstadoProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ExistenciaVinculoEstudianteProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteVinculadoProyectoGradoRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudianteVinculadoProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.PropiedadCoordinadorProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.CoordinadorProyectoGradoPropietarioRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoNoFinalizadoRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.CoordinadorProyectoGradoPropietarioRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoNoFinalizadoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class DestituirEstudianteProyectoGradoValidatorImpl implements DestituirEstudianteProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstadoProyectoGradoExisteRule estadoProyectoGradoExisteRule;
    private final CoordinadorProyectoGradoPropietarioRule coordinadorPropietarioRule;
    private final EstudianteVinculadoProyectoGradoRule estudianteVinculadoRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;

    public DestituirEstudianteProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estadoProyectoGradoExisteRule = new EstadoProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioRule = new CoordinadorProyectoGradoPropietarioRuleImpl();
        this.estudianteVinculadoRule = new EstudianteVinculadoProyectoGradoRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
    }

    @Override
    public void validar(DestitucionEstudianteProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        EstadoProyectoGradoDomain estadoActual, boolean vinculado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        estadoProyectoGradoExisteRule.validar(
                new ExistenciaEstadoProyectoGrado(entrada.getProyectoGrado(), !estadoActual.esVacio()));
        coordinadorPropietarioRule.validar(new PropiedadCoordinadorProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getCoordinador(), entrada.getCoordinador()));
        estudianteVinculadoRule.validar(new ExistenciaVinculoEstudianteProyectoGrado(
                entrada.getProyectoGrado(), entrada.getEstudiante(), vinculado));
        proyectoGradoNoFinalizadoRule.validar(
                new EstadoActualProyectoGrado(entrada.getProyectoGrado(), estadoActual.getEstadoProyecto()));
    }
}
