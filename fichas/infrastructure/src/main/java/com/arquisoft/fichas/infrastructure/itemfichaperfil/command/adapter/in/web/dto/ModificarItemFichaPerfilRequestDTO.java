package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto;

import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModificarItemFichaPerfilRequestDTO(

        @NotBlank(message = ValidationKeys.ItemFichaPerfil.CONTENIDO_OBLIGATORIO)
        @Size(max = FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                message = ValidationKeys.ItemFichaPerfil.CONTENIDO_MAXIMO)
        String contenido) {

    public ModificarItemFichaPerfilCommand toCommand(UUID item, UUID estudiante) {
        return new ModificarItemFichaPerfilCommand(item, contenido, estudiante);
    }
}
