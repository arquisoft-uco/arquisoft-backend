package com.arquisoft.entregables.application.artefacto.command.validator.impl;

import com.arquisoft.entregables.application.artefacto.command.validator.RegistrarVersionCerradaArtefactoValidator;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarVersionCerradaArtefactoValidatorImpl implements RegistrarVersionCerradaArtefactoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;

    public RegistrarVersionCerradaArtefactoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(RegistroVersionCerradaArtefactoDomain registro, ProyectoGradoDomain proyecto) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(
                registro.getArtefacto().getProyectoGrado(), !proyecto.esVacio()));
    }
}
