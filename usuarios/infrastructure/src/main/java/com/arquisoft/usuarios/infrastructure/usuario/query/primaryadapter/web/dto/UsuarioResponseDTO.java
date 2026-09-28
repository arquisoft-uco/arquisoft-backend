package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UsuarioResponseDTO(
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
        boolean esRepresentanteComite
        // TODO HU233: boolean esAdministrador
        // TODO HU242: boolean esBibliotecario
        // TODO HU252: boolean esJurado
) {
}
