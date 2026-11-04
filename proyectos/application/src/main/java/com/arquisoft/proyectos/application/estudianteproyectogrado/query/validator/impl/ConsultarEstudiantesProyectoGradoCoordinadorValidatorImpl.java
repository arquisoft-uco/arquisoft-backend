package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarEstudiantesProyectoGradoCoordinadorValidator;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.model.PropiedadCoordinadorProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.CoordinadorProyectoGradoPropietarioRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.CoordinadorProyectoGradoPropietarioRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarEstudiantesProyectoGradoCoordinadorValidatorImpl
        implements ConsultarEstudiantesProyectoGradoCoordinadorValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final CoordinadorProyectoGradoPropietarioRule coordinadorPropietarioRule;

    public ConsultarEstudiantesProyectoGradoCoordinadorValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioRule = new CoordinadorProyectoGradoPropietarioRuleImpl();
    }

    @Override
    public void validar(UUID proyectoGrado, UUID coordinadorEsperado, UUID coordinadorSolicitante) {
        proyectoGradoExisteRule.validar(
                new ExistenciaProyectoGrado(proyectoGrado, !UtilUUID.esPorDefecto(coordinadorEsperado)));
        coordinadorPropietarioRule.validar(
                new PropiedadCoordinadorProyectoGrado(proyectoGrado, coordinadorEsperado, coordinadorSolicitante));
    }
}
