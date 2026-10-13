package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.fichas.fichaperfil;

import java.time.Instant;
import java.util.List;

public record FichaPerfilAprobadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String fichaPerfilId,
        String tituloProyecto,
        String coordinadorId,
        List<IntegrantePayload> estudiantes
) {

    public record IntegrantePayload(String estudiante) {
    }
}
