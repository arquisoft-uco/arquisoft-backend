package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

public interface GenerarEntregableProyectoGradoValidator {

    void validar(GeneracionEntregableProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                 boolean asesorVigente, boolean existeVigente);
}
