package com.arquisoft.fichas.application.fichaperfil.command.secondaryport;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;

import java.util.Optional;
import java.util.UUID;

public interface FichaPerfilOutputPort {

    void registrarFicha(FichaPerfilEntity ficha);

    void actualizarTitulo(UUID fichaPerfil, String tituloProyecto);

    void actualizarAsesor(UUID fichaPerfil, UUID nuevoAsesorFicha);

    Optional<FichaPerfilEntity> buscarPorId(UUID id);

    boolean existePorId(UUID id);

    boolean existePorTituloProyecto(String titulo);

    boolean existeTituloEnOtraFicha(UUID fichaPerfil, String titulo);
}
