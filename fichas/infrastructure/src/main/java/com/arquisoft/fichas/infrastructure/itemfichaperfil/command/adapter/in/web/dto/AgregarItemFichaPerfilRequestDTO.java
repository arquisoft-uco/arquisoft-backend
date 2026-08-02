package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto;

import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AgregarItemFichaPerfilRequestDTO(

        @NotBlank(message = ValidationKeys.ItemFichaPerfil.TIPO_OBLIGATORIO)
        String tipoItem,

        @NotBlank(message = ValidationKeys.ItemFichaPerfil.CONTENIDO_OBLIGATORIO)
        @Size(max = FichasLimits.ItemFichaPerfil.CONTENIDO_MAX,
                message = ValidationKeys.ItemFichaPerfil.CONTENIDO_MAXIMO)
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
