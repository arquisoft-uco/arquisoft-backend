package com.arquisoft.fichas.application.representantecomite.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionRepresentanteComiteResult {

    record Agregada(UUID representanteComite) implements AgregacionRepresentanteComiteResult {}

    record Reactivada(UUID representanteComite) implements AgregacionRepresentanteComiteResult {}

    record Duplicada(UUID representanteComite) implements AgregacionRepresentanteComiteResult {}

    record Descartada(UUID representanteComite, Instant ocurridoEnVigente)
            implements AgregacionRepresentanteComiteResult {}
}
