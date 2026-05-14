package com.arquisoft.seguridad.domain.model;

import com.arquisoft.seguridad.domain.event.UsuarioCreadoEvent;
import com.arquisoft.shared.events.EventEmittingEntity;

import java.util.UUID;

/**
 * Aggregate Root del bounded context {@code seguridad}.
 *
 * <p>Representa un usuario del sistema con su rol asignado.
 * Constructor privado — solo se instancia a través de los factory methods:
 * <ul>
 *   <li>{@link #crear(String, UsuarioRole)} — crea un nuevo usuario, valida invariantes
 *       y <b>acumula</b> el evento {@link UsuarioCreadoEvent} en {@code unPublishedEvents}.
 *       El use case drena y publica los eventos tras persistir el aggregate.</li>
 *   <li>{@link #rebuild(UUID, String, UsuarioRole)} — reconstruye desde persistencia sin
 *       emitir eventos (el usuario ya existe, no hay novedad de negocio que comunicar).</li>
 * </ul>
 *
 * <p>Sin Spring, sin Lombok, sin JPA — Java puro.
 */
public final class Usuario extends EventEmittingEntity {

    private final UUID id;
    private final String email;
    private final UsuarioRole rol;

    private Usuario(UUID id, String email, UsuarioRole rol) {
        this.id    = id;
        this.email = email;
        this.rol   = rol;
    }

    /**
     * Crea un nuevo usuario, valida sus invariantes y emite {@link UsuarioCreadoEvent}.
     *
     * @param email email único del usuario (no puede ser nulo ni vacío)
     * @param rol   rol asignado al usuario en el sistema (no puede ser nulo)
     * @return nuevo aggregate con el evento acumulado en {@code unPublishedEvents}
     */
    public static Usuario crear(String email, UsuarioRole rol) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email del usuario no puede ser vacío");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol del usuario no puede ser nulo");
        }

        Usuario usuario = new Usuario(UUID.randomUUID(), email.trim().toLowerCase(), rol);
        // El aggregate decide que su creación es un evento relevante para el sistema.
        // El evento queda acumulado en memoria — el use case lo drenará tras persistir.
        usuario.publishEvent(new UsuarioCreadoEvent(usuario.id, usuario.email, usuario.rol.getCode()));
        return usuario;
    }

    /**
     * Reconstruye un usuario desde persistencia sin emitir eventos.
     * Usado por el repositorio cuando carga un aggregate ya existente.
     */
    public static Usuario rebuild(UUID id, String email, UsuarioRole rol) {
        return new Usuario(id, email, rol);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UsuarioRole getRol() {
        return rol;
    }
}
