package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarCompanerosProyectoGradoValidator;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ExistenciaVinculoEstudianteProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteVinculadoProyectoGradoRule;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl.EstudianteVinculadoProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarCompanerosProyectoGradoValidatorImpl implements ConsultarCompanerosProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final EstudianteVinculadoProyectoGradoRule estudianteVinculadoRule;

    public ConsultarCompanerosProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.estudianteVinculadoRule = new EstudianteVinculadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(CompaneroProyectoGradoCriteria criteria, boolean proyectoExiste, boolean vinculado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(criteria.proyectoGrado(), proyectoExiste));
        estudianteVinculadoRule.validar(new ExistenciaVinculoEstudianteProyectoGrado(
                criteria.proyectoGrado(), criteria.estudiante(), vinculado));
    }
}
