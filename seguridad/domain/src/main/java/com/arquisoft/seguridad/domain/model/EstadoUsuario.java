package com.arquisoft.seguridad.domain.model;

/**
 * Estados posibles de un usuario en el sistema Arquisoft.
 *
 * <p>El código de cada estado coincide exactamente con el valor almacenado en la
 * tabla catálogo {@code usuarios.estado_usuario} (columna {@code nombre}).
 *
 * <ul>
 *   <li>{@code ACTIVO}   — el usuario puede operar normalmente en el sistema.</li>
 *   <li>{@code INACTIVO} — el usuario está deshabilitado y no puede iniciar sesión.</li>
 * </ul>
 */
public enum EstadoUsuario {

    ACTIVO("ACTIVO", "Usuario habilitado para operar en el sistema"),
    INACTIVO("INACTIVO", "Usuario deshabilitado; no puede iniciar sesión");

    private final String code;
    private final String description;

    EstadoUsuario(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Busca el estado a partir del código exacto almacenado en la tabla catálogo.
     *
     * @param code valor de la columna {@code nombre} en {@code estado_usuario}
     * @throws IllegalArgumentException si el código no corresponde a ningún estado conocido
     */
    public static EstadoUsuario fromCode(String code) {
        for (EstadoUsuario estado : EstadoUsuario.values()) {
            if (estado.code.equals(code)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de usuario desconocido: " + code);
    }
}
