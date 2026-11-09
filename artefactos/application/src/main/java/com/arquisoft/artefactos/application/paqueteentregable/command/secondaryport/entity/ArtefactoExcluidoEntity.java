package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity;

import java.util.UUID;

public record ArtefactoExcluidoEntity(UUID artefacto, String nombreDocumento, String motivo, int ultimaVersion) {
}
