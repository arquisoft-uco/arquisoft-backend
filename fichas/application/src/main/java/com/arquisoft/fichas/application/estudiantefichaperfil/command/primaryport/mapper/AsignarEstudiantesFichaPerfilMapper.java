package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

public final class AsignarEstudiantesFichaPerfilMapper {

    private AsignarEstudiantesFichaPerfilMapper() {}

    public static AgregacionEstudiantesFichaPerfilDomain toDomain(
            AsignarEstudiantesFichaPerfilCommand command) {
        var relaciones = EstudianteFichaPerfilDomain.crear(command.fichaPerfil(), command.estudiantes());

        return AgregacionEstudiantesFichaPerfilDomain.crear(relaciones);
    }
}
