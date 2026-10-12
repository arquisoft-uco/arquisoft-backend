package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.primaryport.mapper.AsignarEstadoInicialProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.mapper.AsignarEstudiantesProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.RegistrarProyectoGradoCommand;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.RegistroProyectoGradoDomain;

public final class RegistrarProyectoGradoMapper {

    private RegistrarProyectoGradoMapper() {}

    public static RegistroProyectoGradoDomain toDomain(RegistrarProyectoGradoCommand command) {
        var proyecto = ProyectoGradoDomain.crear(
                command.fichaPerfil(), command.tituloProyecto(), command.coordinador());
        var estadoInicial = AsignarEstadoInicialProyectoGradoMapper.toDomain(proyecto.getId());
        var estudiantes = AsignarEstudiantesProyectoGradoMapper.toDomain(
                proyecto.getId(), proyecto.getCoordinador(), command.estudiantes());
        return RegistroProyectoGradoDomain.crear(proyecto, estadoInicial, estudiantes);
    }
}
