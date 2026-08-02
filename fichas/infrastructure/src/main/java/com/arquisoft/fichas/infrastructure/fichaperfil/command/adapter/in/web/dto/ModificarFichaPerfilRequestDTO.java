package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModificarFichaPerfilRequestDTO(

        @NotBlank(message = ValidationKeys.FichaPerfil.TITULO_OBLIGATORIO)
        @Size(max = FichasLimits.FichaPerfil.TITULO_MAX,
                message = ValidationKeys.FichaPerfil.TITULO_MAXIMO)
        String tituloProyecto) {

    public ModificarFichaPerfilCommand toCommand(UUID fichaPerfil, UUID estudiante) {
        return new ModificarFichaPerfilCommand(fichaPerfil, estudiante, tituloProyecto);
    }
}
