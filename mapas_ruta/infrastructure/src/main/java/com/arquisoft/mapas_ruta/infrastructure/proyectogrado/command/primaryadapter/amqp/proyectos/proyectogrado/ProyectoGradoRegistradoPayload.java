package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public record ProyectoGradoRegistradoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String fichaPerfilId,
        String tituloProyecto,
        CoordinadorPayload coordinador
) {

    public record CoordinadorPayload(String id) {
    }

    public String coordinadorId() {
        return UtilObjeto.esNulo(coordinador) ? null : coordinador.id();
    }
}
