package com.arquisoft.proyectos.application.proyectogrado.command.result.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public final class RegistroProyectoGradoResultMapper {

    private RegistroProyectoGradoResultMapper() {}

    public static RegistroProyectoGradoResult.Registrado toResultRegistrado(ProyectoGradoDomain proyecto) {
        return new RegistroProyectoGradoResult.Registrado(proyecto.getId(), proyecto.getFichaPerfil());
    }

    public static RegistroProyectoGradoResult.Duplicado toResultDuplicado(ProyectoGradoDomain proyecto) {
        return new RegistroProyectoGradoResult.Duplicado(proyecto.getFichaPerfil());
    }
}
