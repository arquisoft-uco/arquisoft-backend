package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

public interface RegistrarResultadoEvaluacionEntregableProyectoGradoValidator {

    void validar(ResultadoEvaluacionEntregableProyectoGradoDomain resultado, EntregableProyectoGradoDomain entregable,
                 EstadoEntregableProyectoGradoDomain estadoActual, ProyectoGradoDomain proyecto);
}
