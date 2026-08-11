package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

import java.util.List;

public final class AsignarEstudiantesFichaPerfilMapper {

    private AsignarEstudiantesFichaPerfilMapper() {}

    public static List<EstudianteFichaPerfilDomain> toDomain(AsignarEstudiantesFichaPerfilCommand command) {
        return EstudianteFichaPerfilDomain.crear(command.fichaPerfil(), command.estudiantes());
    }
}
