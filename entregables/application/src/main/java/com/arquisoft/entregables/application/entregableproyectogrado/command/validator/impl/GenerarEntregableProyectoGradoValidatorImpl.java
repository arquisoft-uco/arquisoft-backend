package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.model.ArtefactosDelProyecto;
import com.arquisoft.entregables.domain.artefacto.rules.ArtefactosParaPaqueteRule;
import com.arquisoft.entregables.domain.artefacto.rules.impl.ArtefactosParaPaqueteRuleImpl;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
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

import java.util.List;
import java.util.UUID;

@Component
public class GenerarEntregableProyectoGradoValidatorImpl implements GenerarEntregableProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;
    private final ProyectoGradoNoFinalizadoRule proyectoGradoNoFinalizadoRule;
    private final ProyectoGradoNoCanceladoRule proyectoGradoNoCanceladoRule;
    private final EntregableVigenteUnicoRule entregableVigenteUnicoRule;
    private final ArtefactosParaPaqueteRule artefactosParaPaqueteRule;

    public GenerarEntregableProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
        this.proyectoGradoNoFinalizadoRule = new ProyectoGradoNoFinalizadoRuleImpl();
        this.proyectoGradoNoCanceladoRule = new ProyectoGradoNoCanceladoRuleImpl();
        this.entregableVigenteUnicoRule = new EntregableVigenteUnicoRuleImpl();
        this.artefactosParaPaqueteRule = new ArtefactosParaPaqueteRuleImpl();
    }

    @Override
    public void validar(GeneracionEntregableProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                        boolean asesorVigente, UUID asesorVigenteProyecto, boolean existeVigente,
                        List<ArtefactoDomain> artefactosVigentes) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));
        asesorVigenteRule.validar(new VigenciaAsesor(entrada.getAsesor(), asesorVigente));
        asesorAsignadoProyectoGradoRule.validar(new VigenciaAsesorProyectoGrado(
                entrada.getProyectoGrado(), entrada.getAsesor(), asesorVigenteProyecto));
        proyectoGradoNoFinalizadoRule.validar(new EstadoActualProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getNombre(), proyecto.getEstadoProyecto()));
        proyectoGradoNoCanceladoRule.validar(new EstadoActualProyectoGrado(
                entrada.getProyectoGrado(), proyecto.getNombre(), proyecto.getEstadoProyecto()));
        entregableVigenteUnicoRule.validar(new ExistenciaEntregableVigente(
                entrada.getProyectoGrado(), proyecto.getNombre(), existeVigente));
        artefactosParaPaqueteRule.validar(new ArtefactosDelProyecto(
                entrada.getProyectoGrado(), proyecto.getNombre(), artefactosVigentes.size()));
    }
}
