package com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;

import java.util.List;
import java.util.UUID;

public interface EstadoFichaPerfilQueryOutputPort {

    List<EstadoFichaPerfilReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante);
}
