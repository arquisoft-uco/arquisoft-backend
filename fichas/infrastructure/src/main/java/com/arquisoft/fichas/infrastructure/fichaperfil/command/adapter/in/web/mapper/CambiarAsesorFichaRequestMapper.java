package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.CambiarAsesorFichaRequestDTO;

import java.util.UUID;

public final class CambiarAsesorFichaRequestMapper {

    private CambiarAsesorFichaRequestMapper() {}

    public static CambiarAsesorFichaCommand toCommand(CambiarAsesorFichaRequestDTO dto, UUID fichaPerfil) {
        return CambiarAsesorFichaCommand.crear(fichaPerfil, dto.asesorFicha());
    }
}
