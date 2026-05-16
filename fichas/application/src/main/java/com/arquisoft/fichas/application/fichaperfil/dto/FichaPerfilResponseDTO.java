package com.arquisoft.fichas.application.fichaperfil.dto;

import com.arquisoft.fichas.application.asesorficha.dto.AsesorFichaResponseDTO;
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
public class FichaPerfilResponseDTO {

    private UUID id;
    private String tituloProyecto;
    private AsesorFichaResponseDTO asesorFicha;

    public static FichaPerfilResponseDTO fromDomain(FichaPerfil ficha) {
        return FichaPerfilResponseDTO.builder()
                .id(ficha.getId())
                .tituloProyecto(ficha.getTituloProyecto())
                .asesorFicha(AsesorFichaResponseDTO.fromDomain(ficha.getAsesorFicha()))
                .build();
    }
}
