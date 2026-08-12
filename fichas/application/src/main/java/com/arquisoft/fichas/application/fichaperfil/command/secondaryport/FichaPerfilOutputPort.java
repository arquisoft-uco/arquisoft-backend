package com.arquisoft.fichas.application.fichaperfil.command.secondaryport;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

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
}
