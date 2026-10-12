package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator;

import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface AsignarAsesorProyectoGradoValidator {

    void validar(AsignacionAsesorProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                 EstadoProyectoGradoDomain estadoActual, AsesorDomain asesor, boolean yaAsignado,
                 long asignadosActuales);
}
