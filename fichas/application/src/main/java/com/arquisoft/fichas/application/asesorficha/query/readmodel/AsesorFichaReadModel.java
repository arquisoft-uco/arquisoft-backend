package com.arquisoft.fichas.application.asesorficha.query.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorFichaReadModel {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
}
