package com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.ConsultarHistorialEstadosProyectoGradoValidator;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.AccesoEstadosProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.UsuarioConAccesoEstadosProyectoGradoRule;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl.UsuarioConAccesoEstadosProyectoGradoRuleImpl;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.proyectos.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarHistorialEstadosProyectoGradoValidatorImpl implements ConsultarHistorialEstadosProyectoGradoValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final UsuarioConAccesoEstadosProyectoGradoRule usuarioConAccesoRule;

    public ConsultarHistorialEstadosProyectoGradoValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.usuarioConAccesoRule = new UsuarioConAccesoEstadosProyectoGradoRuleImpl();
    }

    @Override
    public void validar(HistorialEstadosProyectoGradoCriteria criteria, UUID coordinadorDelProyecto, boolean asesorAsignado) {
        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(
                criteria.proyectoGrado(), !UtilUUID.esPorDefecto(coordinadorDelProyecto)));
        usuarioConAccesoRule.validar(new AccesoEstadosProyectoGrado(
                criteria.proyectoGrado(), criteria.usuario(), coordinadorDelProyecto, asesorAsignado));
    }
}
