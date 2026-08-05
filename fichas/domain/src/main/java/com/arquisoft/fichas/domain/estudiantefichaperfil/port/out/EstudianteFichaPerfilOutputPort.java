package com.arquisoft.fichas.domain.estudiantefichaperfil.port.out;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilDomain;

import java.util.UUID;

public interface EstudianteFichaPerfilOutputPort {

    void guardar(EstudianteFichaPerfilDomain relacion);

    boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);

    long contarPorFichaPerfilId(UUID fichaPerfilId);

    void eliminar(UUID fichaPerfilId, UUID estudianteId);
}
