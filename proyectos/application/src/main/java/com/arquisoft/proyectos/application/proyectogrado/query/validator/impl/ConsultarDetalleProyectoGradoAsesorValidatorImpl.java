package com.arquisoft.proyectos.application.proyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.validator.ConsultarDetalleProyectoGradoAsesorValidator;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ExistenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl.AsesorProyectoGradoExisteRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarDetalleProyectoGradoAsesorValidatorImpl implements ConsultarDetalleProyectoGradoAsesorValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final AsesorProyectoGradoExisteRule asesorProyectoGradoExisteRule;

    public ConsultarDetalleProyectoGradoAsesorValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.asesorProyectoGradoExisteRule = new AsesorProyectoGradoExisteRuleImpl();
    }

    @Override
    public void validar(DetalleProyectoGradoAsesorCriteria criteria, boolean proyectoExiste, boolean asesorAsignado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(criteria.proyectoGrado(), proyectoExiste));
        asesorProyectoGradoExisteRule.validar(new ExistenciaAsesorProyectoGrado(
                criteria.asesor(), criteria.proyectoGrado(), asesorAsignado));
    }
}
