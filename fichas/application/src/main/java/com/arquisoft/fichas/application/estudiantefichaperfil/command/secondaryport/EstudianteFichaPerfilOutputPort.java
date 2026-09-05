package com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;

import java.util.List;
import java.util.UUID;

public interface EstudianteFichaPerfilOutputPort {

    void vincularEstudiante(EstudianteFichaPerfilEntity relacion);

    boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);

    long contarPorFichaPerfilId(UUID fichaPerfilId);

    void desvincularEstudiante(UUID fichaPerfilId, UUID estudianteId);

    List<UUID> obtenerEstudiantesDeFicha(UUID fichaPerfilId);
}
