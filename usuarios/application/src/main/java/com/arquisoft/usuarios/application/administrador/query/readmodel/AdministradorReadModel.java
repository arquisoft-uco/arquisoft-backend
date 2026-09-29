package com.arquisoft.usuarios.application.administrador.query.readmodel;

import java.util.UUID;

public record AdministradorReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente
) {
}
