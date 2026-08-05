package com.arquisoft.fichas.application.estudiantefichaperfil.command.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilDomain;

import java.util.List;

public final class AsignarEstudiantesFichaPerfilMapper {

    private AsignarEstudiantesFichaPerfilMapper() {}

    public static List<EstudianteFichaPerfilDomain> toDomain(AsignarEstudiantesFichaPerfilCommand command) {
        return EstudianteFichaPerfilDomain.crear(command.fichaPerfil(), command.estudiantes());
    }
}
