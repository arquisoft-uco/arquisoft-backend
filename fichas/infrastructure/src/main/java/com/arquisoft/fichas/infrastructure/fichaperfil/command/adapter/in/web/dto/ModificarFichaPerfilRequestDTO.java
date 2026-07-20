package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ModificarFichaPerfilRequestDTO(

        @NotBlank
        @Size(max = 100)
        String tituloProyecto) {

    public ModificarFichaPerfilCommand toCommand(UUID fichaId, UUID estudianteId) {
        return new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloProyecto);
    }
}
