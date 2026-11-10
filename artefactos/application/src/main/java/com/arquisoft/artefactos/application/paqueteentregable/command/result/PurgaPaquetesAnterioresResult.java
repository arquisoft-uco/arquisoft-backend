package com.arquisoft.artefactos.application.paqueteentregable.command.result;

import java.util.UUID;

public record PurgaPaquetesAnterioresResult(UUID proyectoGrado, int eliminados) {
}
