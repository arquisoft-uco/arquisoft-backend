package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.entregables.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableVigente;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableVigenteUnicoRule;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl.EntregableVigenteUnicoRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoNoCanceladoRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoNoFinalizadoRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoNoCanceladoRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoNoFinalizadoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarEntregableProyectoGradoValidatorImpl implements GenerarEntregableProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;
    private final ProyectoGradoNoCanceladoRule proyectoGradoNoCanceladoRule;
    private final EntregableVigenteUnicoRule entregableVigenteUnicoRule;

    public GenerarEntregableProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
        this.proyectoGradoNoCanceladoRule = new ProyectoGradoNoCanceladoRuleImpl();
        this.entregableVigenteUnicoRule = new EntregableVigenteUnicoRuleImpl();
    }

    @Override
    public void validar(GeneracionEntregableProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        boolean asesorVigente, boolean existeVigente) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        asesorVigenteRule.validar(new VigenciaAsesor(entrada.getAsesor(), asesorVigente));
        proyectoGradoNoFinalizadoRule.validar(new EstadoActualProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getNombre(), proyecto.getEstadoProyecto()));
        proyectoGradoNoCanceladoRule.validar(new EstadoActualProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getNombre(), proyecto.getEstadoProyecto()));
        entregableVigenteUnicoRule.validar(new ExistenciaEntregableVigente(
                entrada.getProyectoGrado(), proyecto.getNombre(), existeVigente));
    }
}
