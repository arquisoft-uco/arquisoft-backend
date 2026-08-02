package com.arquisoft.fichas.domain.fichaperfil.port.out;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;

import java.util.Optional;
import java.util.UUID;

public interface FichaPerfilOutputPort {

    void guardar(FichaPerfilAggregate ficha);

    Optional<FichaPerfilAggregate> buscarPorId(UUID id);

    boolean existePorId(UUID id);

    boolean existePorTituloProyecto(String titulo);
}
