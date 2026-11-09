package com.arquisoft.proyectos.application.proyectogrado.command.validator.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.CambiarEstadoProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ExistenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.EstadoProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.model.TransicionEstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoConEntregableRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.TransicionEstadoProyectoPermitidaRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.UltimoEntregableDefinitivoRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoConEntregableRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.TransicionEstadoProyectoPermitidaRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.UltimoEntregableDefinitivoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoProyectoGradoValidatorImpl implements CambiarEstadoProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstadoProyectoGradoExisteRule estadoProyectoGradoExisteRule;
    private final AsesorProyectoGradoExisteRule asesorProyectoGradoExisteRule;
    private final TransicionEstadoProyectoPermitidaRule transicionEstadoProyectoPermitidaRule;
    private final ProyectoGradoConEntregableRule proyectoGradoConEntregableRule;
    private final UltimoEntregableDefinitivoRule ultimoEntregableDefinitivoRule;

    public CambiarEstadoProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estadoProyectoGradoExisteRule = new EstadoProyectoGradoExisteRuleImpl();
        this.asesorProyectoGradoExisteRule = new AsesorProyectoGradoExisteRuleImpl();
        this.transicionEstadoProyectoPermitidaRule = new TransicionEstadoProyectoPermitidaRuleImpl();
        this.proyectoGradoConEntregableRule = new ProyectoGradoConEntregableRuleImpl();
        this.ultimoEntregableDefinitivoRule = new UltimoEntregableDefinitivoRuleImpl();
    }

    @Override
    public void validar(CambioEstadoProyectoGradoDomain cambio, ProyectoGradoDomain proyecto,
                        EstadoProyectoGradoDomain estadoActual, boolean asesorAsignado,
                        EntregableProyectoGradoDomain ultimoEntregable) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(cambio.getProyectoGrado(), !proyecto.esVacio()));
        estadoProyectoGradoExisteRule.validar(
                new ExistenciaEstadoProyectoGrado(cambio.getProyectoGrado(), !estadoActual.esVacio()));
        asesorProyectoGradoExisteRule.validar(new ExistenciaAsesorProyectoGrado(
                cambio.getAsesor(), cambio.getProyectoGrado(), asesorAsignado));
        transicionEstadoProyectoPermitidaRule.validar(new TransicionEstadoProyecto(
                cambio.getProyectoGrado(), proyecto.getTituloProyecto(),
                estadoActual.getEstadoProyecto(), cambio.getEstadoNuevo()));
        proyectoGradoConEntregableRule.validar(new ExistenciaUltimoEntregable(
                cambio.getProyectoGrado(), proyecto.getTituloProyecto(), !ultimoEntregable.esVacio()));
        ultimoEntregableDefinitivoRule.validar(new EstadoUltimoEntregable(
                cambio.getProyectoGrado(), proyecto.getTituloProyecto(),
                ultimoEntregable.getVersion(), ultimoEntregable.getEstadoEntregable()));
    }
}
