package com.arquisoft.fichas.domain.fichaperfil.port.out;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;

import java.util.Optional;
import java.util.UUID;

public interface FichaPerfilOutputPort {

    void guardar(FichaPerfilDomain ficha);

    Optional<FichaPerfilDomain> buscarPorId(UUID id);

    boolean existePorId(UUID id);

    boolean existePorTituloProyecto(String titulo);

    boolean esEstudiantePropietario(PropietarioFichaCriteria criteria);
}
