package com.arquisoft.usuarios.domain.representantecomitecurriculum.model;

import java.util.UUID;

public record DisponibilidadRepresentante(
        UUID usuario,
        boolean yaEsRepresentante
) {
}
