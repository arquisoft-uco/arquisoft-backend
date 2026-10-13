package com.arquisoft.fichas.application.representantecomite.command.result.mapper;

import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

import java.time.Instant;

public final class AgregacionRepresentanteComiteResultMapper {

    private AgregacionRepresentanteComiteResultMapper() {}

    public static AgregacionRepresentanteComiteResult.Agregada toResultAgregada(
            RepresentanteComiteDomain representanteComite) {
        return new AgregacionRepresentanteComiteResult.Agregada(representanteComite.getId());
    }

    public static AgregacionRepresentanteComiteResult.Reactivada toResultReactivada(
            RepresentanteComiteDomain representanteComite) {
        return new AgregacionRepresentanteComiteResult.Reactivada(representanteComite.getId());
    }

    public static AgregacionRepresentanteComiteResult.Duplicada toResultDuplicada(
            RepresentanteComiteDomain representanteComite) {
        return new AgregacionRepresentanteComiteResult.Duplicada(representanteComite.getId());
    }

    public static AgregacionRepresentanteComiteResult.Descartada toResultDescartada(
            RepresentanteComiteDomain representanteComite, Instant ocurridoEnVigente) {
        return new AgregacionRepresentanteComiteResult.Descartada(representanteComite.getId(), ocurridoEnVigente);
    }
}
