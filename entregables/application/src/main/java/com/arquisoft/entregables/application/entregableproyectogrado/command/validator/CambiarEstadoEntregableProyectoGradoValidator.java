package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

public interface CambiarEstadoEntregableProyectoGradoValidator {

    void validar(CambioEstadoEntregableProyectoGradoDomain cambio, EntregableProyectoGradoDomain entregable);
}
