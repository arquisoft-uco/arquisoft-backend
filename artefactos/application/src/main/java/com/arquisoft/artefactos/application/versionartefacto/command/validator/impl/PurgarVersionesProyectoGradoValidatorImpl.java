package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.validator.PurgarVersionesProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoFinalizadoRule;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.artefactos.domain.proyectogrado.rules.impl.ProyectoGradoFinalizadoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class PurgarVersionesProyectoGradoValidatorImpl implements PurgarVersionesProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final ProyectoGradoFinalizadoRule proyectoGradoFinalizadoRule;

    public PurgarVersionesProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.proyectoGradoFinalizadoRule = new ProyectoGradoFinalizadoRuleImpl();
    }

    @Override
    public void validar(ExistenciaProyectoGrado existencia, EstadoProyectoGrado estado) {
        proyectoGradoExisteRule.validar(existencia);
        proyectoGradoFinalizadoRule.validar(estado);
    }
}
