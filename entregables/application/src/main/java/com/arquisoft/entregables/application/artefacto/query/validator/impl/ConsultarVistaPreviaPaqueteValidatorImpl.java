package com.arquisoft.entregables.application.artefacto.query.validator.impl;

import com.arquisoft.entregables.application.artefacto.query.validator.ConsultarVistaPreviaPaqueteValidator;
import com.arquisoft.entregables.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarVistaPreviaPaqueteValidatorImpl implements ConsultarVistaPreviaPaqueteValidator {

    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;

    public ConsultarVistaPreviaPaqueteValidatorImpl() {
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(UUID proyectoGrado, UUID asesorVigente, UUID asesorSolicitante) {
        asesorAsignadoProyectoGradoRule.validar(
                new VigenciaAsesorProyectoGrado(proyectoGrado, asesorSolicitante, asesorVigente));
    }
}
