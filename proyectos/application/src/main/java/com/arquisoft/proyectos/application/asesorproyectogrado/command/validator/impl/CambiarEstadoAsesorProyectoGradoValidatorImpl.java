package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.CambiarEstadoAsesorProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.proyectos.domain.asesor.rules.AsesorExisteRule;
import com.arquisoft.proyectos.domain.asesor.rules.impl.AsesorExisteRuleImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ExistenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.TransicionEstadoAsesor;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.TransicionEstadoAsesorPermitidaRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.TransicionEstadoAsesorPermitidaRuleImpl;
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
public class CambiarEstadoAsesorProyectoGradoValidatorImpl implements CambiarEstadoAsesorProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final CoordinadorProyectoGradoPropietarioRule coordinadorPropietarioRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;
    private final AsesorProyectoGradoExisteRule asesorProyectoGradoExisteRule;
    private final AsesorExisteRule asesorExisteRule;
    private final TransicionEstadoAsesorPermitidaRule transicionPermitidaRule;

    public CambiarEstadoAsesorProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioRule = new CoordinadorProyectoGradoPropietarioRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
        this.asesorProyectoGradoExisteRule = new AsesorProyectoGradoExisteRuleImpl();
        this.asesorExisteRule = new AsesorExisteRuleImpl();
        this.transicionPermitidaRule = new TransicionEstadoAsesorPermitidaRuleImpl();
    }

    @Override
    public void validar(CambioEstadoAsesorProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        AsesorProyectoGradoDomain vinculo, AsesorDomain asesor) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        coordinadorPropietarioRule.validar(new PropiedadCoordinadorProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getCoordinador(), entrada.getCoordinador()));
        proyectoGradoNoFinalizadoRule.validar(
                new EstadoActualProyectoGrado(entrada.getProyectoGrado(), proyecto.getEstadoProyectoGrado()));
        asesorProyectoGradoExisteRule.validar(new ExistenciaAsesorProyectoGrado(
                entrada.getAsesorProyectoGrado(), entrada.getProyectoGrado(), !vinculo.esVacio()));
        asesorExisteRule.validar(new ExistenciaAsesor(vinculo.getAsesor(), !asesor.esVacio()));
        transicionPermitidaRule.validar(new TransicionEstadoAsesor(
                entrada.getAsesorProyectoGrado(), vinculo.getEstadoAsesor(), entrada.getEstadoNuevo()));
    }
}
