package com.arquisoft.fichas.application.asesorficha.dto;

import com.arquisoft.fichas.domain.model.AsesorFicha;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorFichaResponseDTO {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;

    public static AsesorFichaResponseDTO fromDomain(AsesorFicha asesorFicha) {
        return AsesorFichaResponseDTO.builder()
                .id(asesorFicha.getId())
                .identificador(asesorFicha.getIdentificador())
                .nombre(asesorFicha.getNombre())
                .email(asesorFicha.getEmail())
                .build();
    }
}
