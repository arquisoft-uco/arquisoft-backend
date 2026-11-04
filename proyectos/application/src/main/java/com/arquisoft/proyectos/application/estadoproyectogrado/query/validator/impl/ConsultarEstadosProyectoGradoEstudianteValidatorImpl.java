package com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.ConsultarEstadosProyectoGradoEstudianteValidator;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ExistenciaVinculoEstudianteProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteVinculadoProyectoGradoRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudianteVinculadoProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarEstadosProyectoGradoEstudianteValidatorImpl implements ConsultarEstadosProyectoGradoEstudianteValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstudianteVinculadoProyectoGradoRule estudianteVinculadoRule;

    public ConsultarEstadosProyectoGradoEstudianteValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estudianteVinculadoRule = new EstudianteVinculadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(EstadoProyectoGradoEstudianteCriteria criteria, boolean proyectoExiste, boolean vinculado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(criteria.proyectoGrado(), proyectoExiste));
        estudianteVinculadoRule.validar(new ExistenciaVinculoEstudianteProyectoGrado(
                criteria.proyectoGrado(), criteria.estudiante(), vinculado));
    }
}
