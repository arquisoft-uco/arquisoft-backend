package com.arquisoft.repositorio_artefactos.application.administrador.query.readmodel;

import java.util.UUID;

public record AdministradorReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email
) {
}
