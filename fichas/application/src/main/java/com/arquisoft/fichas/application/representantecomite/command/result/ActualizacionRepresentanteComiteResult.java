package com.arquisoft.fichas.application.representantecomite.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionRepresentanteComiteResult {

    record Actualizada(UUID representanteComite) implements ActualizacionRepresentanteComiteResult {}

    record Descartada(UUID representanteComite, Instant ocurridoEnVigente)
            implements ActualizacionRepresentanteComiteResult {}

    record NoReplicado(UUID representanteComite) implements ActualizacionRepresentanteComiteResult {}
}
