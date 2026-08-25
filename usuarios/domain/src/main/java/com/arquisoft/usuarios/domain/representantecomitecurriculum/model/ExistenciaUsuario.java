package com.arquisoft.usuarios.domain.representantecomitecurriculum.model;

import java.util.UUID;

public record ExistenciaUsuario(
        UUID usuario,
        boolean existe
) {
}
