package com.arquisoft.entregables.application.entregableproyectogrado.query.validator.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.validator.ConsultarEntregablesProyectoGradoEstudianteValidator;
import com.arquisoft.entregables.domain.proyectogrado.model.PertenenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.EstudiantePerteneceProyectoGradoRule;
import com.arquisoft.entregables.domain.proyectogrado.rules.impl.EstudiantePerteneceProyectoGradoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEntregablesProyectoGradoEstudianteValidatorImpl
        implements ConsultarEntregablesProyectoGradoEstudianteValidator {

    private final EstudiantePerteneceProyectoGradoRule estudiantePerteneceProyectoGradoRule;

    public ConsultarEntregablesProyectoGradoEstudianteValidatorImpl() {
        this.estudiantePerteneceProyectoGradoRule = new EstudiantePerteneceProyectoGradoRuleImpl();
    }

    @Override
    public void validar(UUID estudiante, UUID proyectoGrado, boolean pertenece) {
        estudiantePerteneceProyectoGradoRule.validar(
                new PertenenciaProyectoGrado(proyectoGrado, estudiante, pertenece));
    }
}
