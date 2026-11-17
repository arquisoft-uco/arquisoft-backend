package com.arquisoft.usuarios.application.usuario.query.readmodel;

import java.util.List;
import java.util.UUID;

public record PerfilUsuarioReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        EstadoPerfilReadModel estado,
        List<RolPerfilReadModel> roles) {
}
