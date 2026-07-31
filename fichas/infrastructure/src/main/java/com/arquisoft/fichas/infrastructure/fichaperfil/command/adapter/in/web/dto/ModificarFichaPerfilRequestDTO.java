package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.shared.message.FichasMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModificarFichaPerfilRequestDTO(

        @NotBlank(message = FichasMessages.FichaPerfil.TITULO_OBLIGATORIO_MSG)
        @Size(max = FichasMessages.FichaPerfil.TITULO_MAX,
                message = FichasMessages.FichaPerfil.TITULO_MAX_MSG)
        String tituloProyecto) {

    public ModificarFichaPerfilCommand toCommand(UUID fichaPerfil, UUID estudiante) {
        return new ModificarFichaPerfilCommand(fichaPerfil, estudiante, tituloProyecto);
    }
}
