package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface CancelarProyectoGradoValidator {

    void validar(CancelacionProyectoGradoDomain cancelacion, ProyectoGradoDomain proyecto,
                 EstadoProyectoGradoDomain estadoActual);
}
