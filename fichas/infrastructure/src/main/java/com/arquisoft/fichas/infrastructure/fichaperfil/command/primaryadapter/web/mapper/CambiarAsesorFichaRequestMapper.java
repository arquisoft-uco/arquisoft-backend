package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.CambiarAsesorFichaRequestDTO;

import java.util.UUID;

public final class CambiarAsesorFichaRequestMapper {

    private CambiarAsesorFichaRequestMapper() {}

    public static CambiarAsesorFichaCommand toCommand(CambiarAsesorFichaRequestDTO dto, UUID fichaPerfil) {
        return CambiarAsesorFichaCommand.crear(fichaPerfil, dto.asesorFicha());
    }
}
