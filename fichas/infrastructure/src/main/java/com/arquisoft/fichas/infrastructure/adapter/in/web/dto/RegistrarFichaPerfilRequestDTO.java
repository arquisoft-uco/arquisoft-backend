package com.arquisoft.fichas.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarFichaPerfilRequestDTO {

    @NotNull(message = "El identificador de la ficha es obligatorio")
    private UUID id;

    @NotBlank(message = "El título del proyecto es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    private String tituloProyecto;

    @NotNull(message = "El identificador del asesor es obligatorio")
    private UUID asesorFichaId;
}
