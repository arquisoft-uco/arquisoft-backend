package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

import java.util.List;
import java.util.UUID;

public final class AsignarEstudiantesFichaPerfilMapper {

    private AsignarEstudiantesFichaPerfilMapper() {}

    public static AgregacionEstudiantesFichaPerfilDomain toDomain(
            AsignarEstudiantesFichaPerfilCommand command) {
        return toDomain(command.fichaPerfil(), command.estudiantes());
    }

    public static AgregacionEstudiantesFichaPerfilDomain toDomain(
            UUID fichaPerfil, List<UUID> estudiantes) {
        var relaciones = EstudianteFichaPerfilDomain.crear(fichaPerfil, estudiantes);

        return AgregacionEstudiantesFichaPerfilDomain.crear(relaciones);
    }
}
