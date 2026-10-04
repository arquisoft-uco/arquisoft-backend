package com.arquisoft.fichas.application.estadofichaperfil.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record EstadoFichaPerfilAsesorReadModel(
        UUID fichaPerfil,
        String tituloProyecto,
        String estadoId,
        String estadoNombre,
        Instant fechaActualizacion
) {
}
