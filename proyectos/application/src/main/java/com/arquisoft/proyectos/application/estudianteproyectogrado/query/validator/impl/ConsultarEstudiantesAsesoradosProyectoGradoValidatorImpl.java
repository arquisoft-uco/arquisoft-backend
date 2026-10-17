package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarEstudiantesAsesoradosProyectoGradoValidator;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ExistenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarEstudiantesAsesoradosProyectoGradoValidatorImpl
        implements ConsultarEstudiantesAsesoradosProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorProyectoGradoExisteRule asesorProyectoGradoExisteRule;

    public ConsultarEstudiantesAsesoradosProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorProyectoGradoExisteRule = new AsesorProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(EstudianteAsesoradoProyectoGradoCriteria criteria,
                        boolean proyectoExiste, boolean asesorAsignado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(criteria.proyectoGrado(), proyectoExiste));
        asesorProyectoGradoExisteRule.validar(new ExistenciaAsesorProyectoGrado(
                criteria.asesor(), criteria.proyectoGrado(), asesorAsignado));
    }
}
