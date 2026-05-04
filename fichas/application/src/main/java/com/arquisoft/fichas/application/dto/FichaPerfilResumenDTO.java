package com.arquisoft.fichas.application.dto;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FichaPerfilResumenDTO {

    private UUID id;
    private String titulo;
    private AsesorResumenDTO asesor;

    public static FichaPerfilResumenDTO fromDomain(FichaPerfil ficha) {
        return FichaPerfilResumenDTO.builder()
                .id(ficha.getId())
                .titulo(ficha.getTituloProyecto())
                .asesor(AsesorResumenDTO.fromDomain(ficha.getAsesorFicha()))
                .build();
    }
}
