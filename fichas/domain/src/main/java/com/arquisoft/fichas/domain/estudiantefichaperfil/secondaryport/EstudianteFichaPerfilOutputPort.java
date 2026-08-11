package com.arquisoft.fichas.domain.estudiantefichaperfil.secondaryport;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

import java.util.UUID;

public interface EstudianteFichaPerfilOutputPort {

    void vincularEstudiante(EstudianteFichaPerfilDomain relacion);

    boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);

    long contarPorFichaPerfilId(UUID fichaPerfilId);

    void desvincularEstudiante(UUID fichaPerfilId, UUID estudianteId);
}
