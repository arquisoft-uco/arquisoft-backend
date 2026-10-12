package com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.ConsultarAsesoresProyectoGradoEstudianteValidator;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ExistenciaVinculoEstudianteProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteVinculadoProyectoGradoRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudianteVinculadoProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarAsesoresProyectoGradoEstudianteValidatorImpl
        implements ConsultarAsesoresProyectoGradoEstudianteValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstudianteVinculadoProyectoGradoRule estudianteVinculadoRule;

    public ConsultarAsesoresProyectoGradoEstudianteValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estudianteVinculadoRule = new EstudianteVinculadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(AsesorProyectoGradoEstudianteCriteria criteria, boolean proyectoExiste, boolean vinculado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(criteria.proyectoGrado(), proyectoExiste));
        estudianteVinculadoRule.validar(new ExistenciaVinculoEstudianteProyectoGrado(
                criteria.proyectoGrado(), criteria.estudiante(), vinculado));
    }
}
