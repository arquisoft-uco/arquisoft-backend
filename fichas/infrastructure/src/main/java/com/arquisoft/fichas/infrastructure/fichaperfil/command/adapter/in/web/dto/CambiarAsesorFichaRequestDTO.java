package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CambiarAsesorFichaRequestDTO(

        @NotBlank(message = ValidationKeys.FichaPerfil.ASESOR_OBLIGATORIO)
        @UuidValido(message = ValidationKeys.FichaPerfil.ASESOR_UUID)
        String asesorFicha) {

    public CambiarAsesorFichaCommand toCommand(UUID fichaPerfil) {
        return new CambiarAsesorFichaCommand(fichaPerfil, UtilUUID.generateUUIDFromString(asesorFicha));
    }
}
