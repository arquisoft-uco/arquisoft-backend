package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator;

import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface CambiarEstadoAsesorProyectoGradoValidator {

    void validar(CambioEstadoAsesorProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                 AsesorProyectoGradoDomain vinculo, AsesorDomain asesor);
}
