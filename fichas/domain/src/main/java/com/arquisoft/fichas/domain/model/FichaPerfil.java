package com.arquisoft.fichas.domain.model;

import com.arquisoft.fichas.domain.event.FichaPerfilCreadaEvent;
import com.arquisoft.shared.domain.AggregateRoot;

import java.util.UUID;

/**
 * Aggregate Root del bounded context {@code fichas}.
 *
 * <p>Representa una ficha de perfil de proyecto de grado con su asesor asignado.
 * Inmutable: constructor privado, campos {@code final}, acceso solo por getters.
 * Sin Spring, sin Lombok, sin JPA — Java puro.
 *
 * <ul>
 *   <li>{@link #build(String, AsesorFicha)} — crea una nueva ficha, genera UUID y publica evento.</li>
 *   <li>{@link #rebuild(UUID, String, AsesorFicha)} — reconstruye desde persistencia, sin evento.</li>
 * </ul>
 */
public final class FichaPerfil extends AggregateRoot {

    private final UUID id;
    private final String tituloProyecto;
    private final AsesorFicha asesorFicha;

    private FichaPerfil(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFicha = asesorFicha;
    }

    /**
     * Crea una nueva FichaPerfil con identidad generada automáticamente.
     */
    public static FichaPerfil build(String tituloProyecto, AsesorFicha asesorFicha) {
        if (tituloProyecto == null || tituloProyecto.isBlank()) {
            throw new IllegalArgumentException("El título del proyecto no puede ser nulo ni vacío");
        }
        String titulo = tituloProyecto.trim();
        if (titulo.length() > 100) {
            throw new IllegalArgumentException("El título del proyecto no puede superar 100 caracteres");
        }
        if (asesorFicha == null) {
            throw new IllegalArgumentException("El asesor de la ficha no puede ser nulo");
        }

        UUID id = UUID.randomUUID();
        FichaPerfil ficha = new FichaPerfil(id, titulo, asesorFicha);
        ficha.publishEvent(new FichaPerfilCreadaEvent(id.toString(), titulo));
        return ficha;
    }

    /**
     * Reconstruye una FichaPerfil desde persistencia.
     */
    public static FichaPerfil rebuild(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        return new FichaPerfil(id, tituloProyecto, asesorFicha);
    }

    public UUID getId() {
        return id;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public AsesorFicha getAsesorFicha() {
        return asesorFicha;
    }
}
