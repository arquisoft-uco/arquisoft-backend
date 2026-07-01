package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
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
public class ModificarFichaPerfilRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String tituloProyecto;

    public ModificarFichaPerfilCommand toCommand(UUID fichaId, UUID estudianteId) {
        return new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloProyecto);
    }
}
