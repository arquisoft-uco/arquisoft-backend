package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

public interface CambiarEstadoEntregableProyectoGradoValidator {

    void validar(CambioEstadoEntregableProyectoGradoDomain cambio, EntregableProyectoGradoDomain entregable,
                 ProyectoGradoDomain proyecto);
}
