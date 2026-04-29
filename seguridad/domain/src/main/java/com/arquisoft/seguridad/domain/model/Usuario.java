package com.arquisoft.seguridad.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Entidad de dominio plana que representa un usuario del sistema Arquisoft.
 *
 * El contexto {@code seguridad} delega la autenticación en Keycloak, por lo que
 * esta entidad NO extiende {@code AggregateRoot}. Los datos de negocio (nombre,
 * estado, roles contextuales) se persisten en PostgreSQL schema {@code usuarios}.
 *
 * Inmutable: constructor privado, campos {@code final}, solo getters.
 * Sin Lombok, sin Spring, sin frameworks externos.
 */
public final class Usuario {

    private final UUID id;
    private final UUID keycloakUserId;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String identificador;
    private final EstadoUsuario estado;
    private final List<UsuarioRole> roles;

    @SuppressWarnings("checkstyle:ParameterNumber")
    private Usuario(
            UUID id,
            UUID keycloakUserId,
            String nombre,
            String apellido,
            String email,
            String identificador,
            EstadoUsuario estado,
            List<UsuarioRole> roles) {
        this.id = id;
        this.keycloakUserId = keycloakUserId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.identificador = identificador;
        this.estado = estado;
        this.roles = Collections.unmodifiableList(roles);
    }

    /**
     * Factory method para crear un nuevo usuario (uso en HUs de escritura futuras).
     * Genera un nuevo UUID como identificador del usuario.
     */
    public static Usuario build(
            UUID keycloakUserId,
            String nombre,
            String apellido,
            String email,
            String identificador,
            EstadoUsuario estado,
            List<UsuarioRole> roles) {
        return new Usuario(
                UUID.randomUUID(),
                keycloakUserId,
                nombre,
                apellido,
                email,
                identificador,
                estado,
                roles);
    }

    /**
     * Factory method para reconstruir un usuario desde persistencia.
     * Recibe el UUID existente — NO genera uno nuevo, NO publica eventos.
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static Usuario rebuild(
            UUID id,
            UUID keycloakUserId,
            String nombre,
            String apellido,
            String email,
            String identificador,
            EstadoUsuario estado,
            List<UsuarioRole> roles) {
        return new Usuario(id, keycloakUserId, nombre, apellido, email, identificador, estado, roles);
    }

    public UUID getId() {
        return id;
    }

    public UUID getKeycloakUserId() {
        return keycloakUserId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentificador() {
        return identificador;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public List<UsuarioRole> getRoles() {
        return roles;
    }
}
