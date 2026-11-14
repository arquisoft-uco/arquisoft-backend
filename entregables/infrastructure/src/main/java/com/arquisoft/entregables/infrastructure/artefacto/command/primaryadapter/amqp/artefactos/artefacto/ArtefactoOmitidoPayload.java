package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import java.time.Instant;

public record ArtefactoOmitidoPayload(String idEvento, Instant ocurridoEn, String artefacto, String proyectoGrado) {
}
