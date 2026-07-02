package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarItemFichaPerfilRequestDTO {

    @NotBlank(message = "El tipo de ítem es obligatorio")
    private String tipoItem;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 7000, message = "El contenido no puede superar 7000 caracteres")
    private String contenido;

    public AgregarItemFichaPerfilCommand toCommand(UUID fichaPerfilId, UUID estudianteId) {
        return new AgregarItemFichaPerfilCommand(
                fichaPerfilId,
                tipoItem,
                contenido,
                estudianteId
        );
    }
}
