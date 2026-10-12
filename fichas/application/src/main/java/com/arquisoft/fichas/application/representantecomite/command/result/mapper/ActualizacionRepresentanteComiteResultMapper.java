package com.arquisoft.fichas.application.representantecomite.command.result.mapper;

import com.arquisoft.fichas.application.representantecomite.command.result.ActualizacionRepresentanteComiteResult;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

import java.time.Instant;

public final class ActualizacionRepresentanteComiteResultMapper {

    private ActualizacionRepresentanteComiteResultMapper() {}

    public static ActualizacionRepresentanteComiteResult.Actualizada toResultActualizada(
            RepresentanteComiteDomain representanteComite) {
        return new ActualizacionRepresentanteComiteResult.Actualizada(representanteComite.getId());
    }

    public static ActualizacionRepresentanteComiteResult.Descartada toResultDescartada(
            RepresentanteComiteDomain representanteComite, Instant ocurridoEnVigente) {
        return new ActualizacionRepresentanteComiteResult.Descartada(representanteComite.getId(), ocurridoEnVigente);
    }

    public static ActualizacionRepresentanteComiteResult.NoReplicado toResultNoReplicado(
            RepresentanteComiteDomain representanteComite) {
        return new ActualizacionRepresentanteComiteResult.NoReplicado(representanteComite.getId());
    }
}
