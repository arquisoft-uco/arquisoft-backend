package com.arquisoft.fichas.application.representantecomite.command.result.mapper;

import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;

import java.time.Instant;

public final class RemocionRepresentanteComiteResultMapper {

    private RemocionRepresentanteComiteResultMapper() {}

    public static RemocionRepresentanteComiteResult.Removida toResultRemovida(RepresentanteComiteDomain representanteComite) {
        return new RemocionRepresentanteComiteResult.Removida(representanteComite.getId());
    }

    public static RemocionRepresentanteComiteResult.Lapida toResultLapida(RepresentanteComiteDomain representanteComite) {
        return new RemocionRepresentanteComiteResult.Lapida(representanteComite.getId());
    }

    public static RemocionRepresentanteComiteResult.Descartada toResultDescartada(
            RepresentanteComiteDomain representanteComite, Instant ocurridoEnVigente) {
        return new RemocionRepresentanteComiteResult.Descartada(representanteComite.getId(), ocurridoEnVigente);
    }
}
