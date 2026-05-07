package com.arquisoft.fichas.domain.model;

import java.util.UUID;

/**
 * Entidad hija que representa al asesor asignado a una FichaPerfil.
 *
 * <p>Tiene identidad propia ({@code id}) pero no es un Aggregate Root.
 * Gestionada directamente en el bounded context {@code fichas}.
 * Java puro: sin Spring, sin Lombok, sin JPA.
 */
public final class AsesorFicha {

    private final UUID id;
    private final String identificador;
    private final String nombre;
    private final String email;

    private AsesorFicha(UUID id, String identificador, String nombre, String email) {
        this.id = id;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
    }

    /**
     * Factory estático para construir un AsesorFicha.
     * Usado tanto para instancias nuevas como para reconstrucción desde persistencia.
     *
     * @param id            identificador único (UUID) del asesor
     * @param identificador código/documento institucional del asesor (máx. 30 chars)
     * @param nombre        nombre completo del asesor
     * @param email         correo institucional del asesor
     * @return instancia de AsesorFicha
     */
    public static AsesorFicha of(UUID id, String identificador, String nombre, String email) {
        return new AsesorFicha(id, identificador, nombre, email);
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
