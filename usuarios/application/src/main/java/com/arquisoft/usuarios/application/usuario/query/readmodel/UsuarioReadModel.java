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
        boolean esCoordinador
        // TODO HU233: boolean esAdministrador
        // TODO HU242: boolean esBibliotecario
        // TODO HU252: boolean esJurado
        // TODO HU255: boolean esRepresentanteComite
) {
}
