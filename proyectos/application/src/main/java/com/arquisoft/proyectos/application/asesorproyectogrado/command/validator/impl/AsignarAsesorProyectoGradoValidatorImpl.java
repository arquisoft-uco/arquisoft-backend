package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.AsignarAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.AsignacionVigenteAsesor;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.CupoAsesoresProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorNoAsignadoProyectoGradoRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoCupoDisponibleRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorVigenteProyectoGradoRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorNoAsignadoProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorProyectoGradoCupoDisponibleRuleImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorVigenteProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.EstadoProyectoGradoExisteRuleImpl;
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
public class AsignarAsesorProyectoGradoValidatorImpl implements AsignarAsesorProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstadoProyectoGradoExisteRule estadoProyectoGradoExisteRule;
    private final CoordinadorProyectoGradoPropietarioRule coordinadorPropietarioRule;
    private final AsesorVigenteProyectoGradoRule asesorVigenteRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;
    private final AsesorNoAsignadoProyectoGradoRule asesorNoAsignadoRule;
    private final AsesorProyectoGradoCupoDisponibleRule cupoDisponibleRule;

    public AsignarAsesorProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estadoProyectoGradoExisteRule = new EstadoProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioRule = new CoordinadorProyectoGradoPropietarioRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteProyectoGradoRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
        this.asesorNoAsignadoRule = new AsesorNoAsignadoProyectoGradoRuleImpl();
        this.cupoDisponibleRule = new AsesorProyectoGradoCupoDisponibleRuleImpl();
    }

    @Override
    public void validar(AsignacionAsesorProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        EstadoProyectoGradoDomain estadoActual, AsesorDomain asesor, boolean yaAsignado,
                        long asignadosActuales) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        estadoProyectoGradoExisteRule.validar(
                new ExistenciaEstadoProyectoGrado(entrada.getProyectoGrado(), !estadoActual.esVacio()));
        coordinadorPropietarioRule.validar(new PropiedadCoordinadorProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getCoordinador(), entrada.getCoordinador()));
        asesorVigenteRule.validar(new VigenciaAsesorProyectoGrado(
                entrada.getAsesor(), !asesor.esVacio(), asesor.estaEliminado()));
        proyectoGradoNoFinalizadoRule.validar(
                new EstadoActualProyectoGrado(entrada.getProyectoGrado(), proyecto.getTituloProyecto(),
                        estadoActual.getEstadoProyecto()));
        asesorNoAsignadoRule.validar(
                new AsignacionVigenteAsesor(entrada.getAsesor(), asesor.getNombre(), entrada.getProyectoGrado(), yaAsignado));
        cupoDisponibleRule.validar(new CupoAsesoresProyectoGrado(asignadosActuales));
    }
}
