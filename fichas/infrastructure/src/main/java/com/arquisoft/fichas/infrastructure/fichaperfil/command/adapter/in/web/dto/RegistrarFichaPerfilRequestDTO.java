package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarFichaPerfilRequestDTO {

    @NotBlank(message = "El título del proyecto es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    private String tituloProyecto;

    @NotNull(message = "El identificador del asesor es obligatorio")
    private UUID asesorFichaId;

    @Size(max = 3, message = "No se pueden asignar más de 3 estudiantes")
    private List<UUID> estudiantesIds;

    public RegistrarFichaPerfilCommand toCommand() {
        return new RegistrarFichaPerfilCommand(tituloProyecto, asesorFichaId, estudiantesIds);
    }
}
