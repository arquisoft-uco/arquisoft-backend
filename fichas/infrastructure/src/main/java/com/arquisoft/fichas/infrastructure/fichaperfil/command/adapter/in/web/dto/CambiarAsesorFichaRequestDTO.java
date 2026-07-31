package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CambiarAsesorFichaRequestDTO(

        @NotBlank(message = FichasMessages.FichaPerfil.ASESOR_OBLIGATORIO_MSG)
        @UuidValido(message = FichasMessages.FichaPerfil.ASESOR_FORMATO_UUID_MSG)
        String asesorFicha) {

    public CambiarAsesorFichaCommand toCommand(UUID fichaPerfil) {
        return new CambiarAsesorFichaCommand(fichaPerfil, UtilUUID.generateUUIDFromString(asesorFicha));
    }
}
