package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModificarItemFichaPerfilRequestDTO(
        @NotBlank(message = "El contenido es obligatorio")
        @Size(max = 7000, message = "El contenido no puede exceder 7000 caracteres")
        String contenido
) {
    public ModificarItemFichaPerfilCommand toCommand(UUID itemId, UUID estudianteId) {
        return new ModificarItemFichaPerfilCommand(itemId, contenido, estudianteId);
    }
}
