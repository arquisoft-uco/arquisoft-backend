package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface AtrasarProyectoGradoValidator {

    void validar(AtrasoProyectoGradoDomain atraso, ProyectoGradoDomain proyecto,
                 EstadoProyectoGradoDomain estadoActual, CoordinadorDomain coordinador);
}
