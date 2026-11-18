package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface CambiarEstadoProyectoGradoValidator {

    void validar(CambioEstadoProyectoGradoDomain cambio, ProyectoGradoDomain proyecto,
                 EstadoProyectoGradoDomain estadoActual, boolean asesorAsignado,
                 EntregableProyectoGradoDomain ultimoEntregable);
}
