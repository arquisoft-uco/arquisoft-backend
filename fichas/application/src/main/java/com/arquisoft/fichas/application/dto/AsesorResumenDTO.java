package com.arquisoft.fichas.application.dto;

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
public class AsesorResumenDTO {

    private UUID id;
    private String nombre;
    private String email;

    public static AsesorResumenDTO fromDomain(AsesorFicha asesor) {
        return AsesorResumenDTO.builder()
                .id(asesor.getId())
                .nombre(asesor.getNombre())
                .email(asesor.getEmail())
                .build();
    }
}
