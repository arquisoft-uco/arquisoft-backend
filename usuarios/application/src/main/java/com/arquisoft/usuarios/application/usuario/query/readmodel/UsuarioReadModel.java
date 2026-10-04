package com.arquisoft.usuarios.application.usuario.query.readmodel;

import java.util.UUID;

public record UsuarioReadModel(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        boolean vigente,
        boolean esEstudiante,
        boolean esAsesor,
        boolean esAsesorFicha,
        boolean esCoordinador,
        boolean esRepresentanteComite,
        boolean esAdministrador,
        boolean esBibliotecario
        // TODO HU252: boolean esJurado
) {
}
