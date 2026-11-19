package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface ReanudarProyectoGradoValidator {

    void validar(ReanudacionProyectoGradoDomain reanudacion, ProyectoGradoDomain proyecto,
                 EstadoProyectoGradoDomain estadoActual, CoordinadorDomain coordinador);
}
