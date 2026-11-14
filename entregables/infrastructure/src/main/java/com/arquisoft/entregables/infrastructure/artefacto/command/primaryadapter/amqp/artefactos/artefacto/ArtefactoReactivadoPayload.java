package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import java.time.Instant;

public record ArtefactoReactivadoPayload(String idEvento, Instant ocurridoEn, String artefacto, String proyectoGrado) {
}
