package com.arquisoft.fichas.domain.estudiantefichaperfil.port.out;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;

import java.util.UUID;

public interface EstudianteFichaPerfilOutputPort {

    void guardar(EstudianteFichaPerfilAggregate relacion);

    boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);

    long contarPorFichaPerfilId(UUID fichaPerfilId);

    void eliminar(UUID fichaPerfilId, UUID estudianteId);
}
