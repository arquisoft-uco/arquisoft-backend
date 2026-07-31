package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.shared.message.FichasMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModificarItemFichaPerfilRequestDTO(

        @NotBlank(message = FichasMessages.ItemFichaPerfil.CONTENIDO_OBLIGATORIO_MSG)
        @Size(max = FichasMessages.ItemFichaPerfil.CONTENIDO_MAX,
                message = FichasMessages.ItemFichaPerfil.CONTENIDO_MAX_MSG)
        String contenido) {

    public ModificarItemFichaPerfilCommand toCommand(UUID item, UUID estudiante) {
        return new ModificarItemFichaPerfilCommand(item, contenido, estudiante);
    }
}
