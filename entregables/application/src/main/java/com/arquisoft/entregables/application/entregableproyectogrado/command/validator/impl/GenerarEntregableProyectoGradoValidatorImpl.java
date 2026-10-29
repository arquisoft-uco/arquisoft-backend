package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.asesor.rules.AsesorVigenteRule;
import com.arquisoft.entregables.domain.asesor.rules.impl.AsesorVigenteRuleImpl;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class GenerarEntregableProyectoGradoValidatorImpl implements GenerarEntregableProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorVigenteRule asesorVigenteRule;

    public GenerarEntregableProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorVigenteRule = new AsesorVigenteRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado proyectoGrado, VigenciaAsesor asesor) {
        proyectoGradoExisteRule.validar(proyectoGrado);
        asesorVigenteRule.validar(asesor);
    }
}
