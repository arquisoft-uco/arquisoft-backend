package com.arquisoft.fichas.application.fichaperfil.query.readmodel;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;

import java.util.UUID;

public record FichaPerfilReadModel(
        UUID id,
        String tituloProyecto,
        AsesorFichaReadModel asesorFicha,
        EstadoFichaPerfilReadModel estado
) {
}
