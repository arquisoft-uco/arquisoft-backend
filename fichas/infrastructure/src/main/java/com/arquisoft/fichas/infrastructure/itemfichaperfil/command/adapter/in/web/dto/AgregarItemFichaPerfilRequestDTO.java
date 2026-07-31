package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.shared.message.FichasMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AgregarItemFichaPerfilRequestDTO(

        @NotBlank(message = FichasMessages.ItemFichaPerfil.TIPO_ITEM_OBLIGATORIO_MSG)
        String tipoItem,

        @NotBlank(message = FichasMessages.ItemFichaPerfil.CONTENIDO_OBLIGATORIO_MSG)
        @Size(max = FichasMessages.ItemFichaPerfil.CONTENIDO_MAX,
                message = FichasMessages.ItemFichaPerfil.CONTENIDO_MAX_MSG)
        String contenido) {

    public AgregarItemFichaPerfilCommand toCommand(UUID fichaPerfil, UUID estudiante) {
        return new AgregarItemFichaPerfilCommand(
                fichaPerfil,
                tipoItem,
                contenido,
                estudiante
        );
    }
}
