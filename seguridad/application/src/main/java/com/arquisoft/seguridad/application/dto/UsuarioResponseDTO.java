package com.arquisoft.seguridad.application.dto;

import com.arquisoft.seguridad.domain.model.Usuario;

import java.util.List;
import java.util.UUID;

/**
 * DTO de salida que representa un usuario en la respuesta de la API.
 *
 * <p>Modelado como {@code record} de Java 21: inmutable, equals/hashCode basados en valor.
 *
 * @param id           identificador único del usuario
 * @param nombre       nombre del usuario
 * @param apellido     apellido del usuario
 * @param email        correo electrónico institucional
 * @param identificador código institucional (cédula o código estudiantil)
 * @param estado       representación string del estado ({@code "ACTIVO"} o {@code "INACTIVO"})
 * @param roles        lista de códigos de roles asignados (ej. {@code ["ESTUDIANTE", "ASESOR"]})
 */
public record UsuarioResponseDTO(
        UUID id,
        String nombre,
        String apellido,
        String email,
        String identificador,
        String estado,
        List<String> roles
) {

    /**
     * Mapea una entidad de dominio {@link Usuario} a este DTO de respuesta.
     *
     * <p>Convierte {@code List<UsuarioRole>} a {@code List<String>} usando
     * {@code UsuarioRole.getCode()} para obtener el código exacto de cada rol.
     *
     * @param usuario entidad de dominio a mapear
     * @return DTO de respuesta con todos los campos mapeados
     */
    public static UsuarioResponseDTO fromDomain(Usuario usuario) {
        List<String> rolesCodigos = usuario.getRoles()
                .stream()
                .map(rol -> rol.getCode())
                .toList();

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getIdentificador(),
                usuario.getEstado().name(),
                rolesCodigos
        );
    }
}
