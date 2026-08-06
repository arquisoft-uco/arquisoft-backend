package com.arquisoft.fichas.domain.fichaperfil.port.out;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;

import java.util.Optional;
import java.util.UUID;

public interface FichaPerfilOutputPort {

    void registrarFicha(FichaPerfilDomain ficha);

    void actualizarTitulo(UUID fichaPerfil, String tituloProyecto);

    void actualizarAsesor(UUID fichaPerfil, UUID nuevoAsesorFicha);

    Optional<FichaPerfilDomain> buscarPorId(UUID id);

    boolean existePorId(UUID id);

    boolean existePorTituloProyecto(String titulo);

    boolean existeTituloEnOtraFicha(UUID fichaPerfil, String titulo);

    boolean esEstudiantePropietario(PropietarioFichaCriteria criteria);
}
