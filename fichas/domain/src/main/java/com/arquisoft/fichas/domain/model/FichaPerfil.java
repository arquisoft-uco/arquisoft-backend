package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;
import java.util.UUID;

/**
 * Aggregate Root del bounded context {@code fichas}.
 *
 * <p>Representa una ficha de perfil de proyecto de grado con su asesor asignado.
 * Constructor privado y setters privados con encapsulamiento estricto.
 * Sin Spring, sin Lombok, sin JPA — Java puro.
 *
 * <ul>
 *   <li>{@link #build(String, AsesorFicha)} — valida todos los campos acumulando errores
 *       (Notification Pattern) y lanza {@link com.arquisoft.shared.validation.DomainValidationException}
 *       con la lista completa si existe al menos uno. Genera UUID solo si la validación pasa.</li>
 *   <li>{@link #rebuild(UUID, String, AsesorFicha)} — reconstruye desde persistencia sin validación.</li>
 * </ul>
 */
public final class FichaPerfil {

    private UUID id;
    private String tituloProyecto;
    private AsesorFicha asesorFicha;

    private FichaPerfil() {}

    // ─── Private setters ──────────────────────────────────────────────────────
    // Cada setter delega sus reglas en DomainValidator, acumula en result
    // y solo asigna el valor cuando el campo no presentó errores.
    // Son la única fuente de verdad por campo: reutilizables desde build()
    // y cualquier método de mutación de negocio.

    private void setId() {
        this.id = UUID.randomUUID();
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        DomainValidator.notBlank(titulo, "tituloProyecto", "FICHA_TITULO_REQUIRED", result);
        DomainValidator.maxLength(titulo, 100, "tituloProyecto", "FICHA_TITULO_TOO_LONG", result);
        if (!result.hasFieldErrors("tituloProyecto")) {
            this.tituloProyecto = titulo.trim();
        }
    }

    private void setAsesorFicha(AsesorFicha asesor, ValidationResult result) {
        DomainValidator.notNull(asesor, "asesorFicha", "FICHA_ASESOR_REQUIRED", result);
        if (!result.hasFieldErrors("asesorFicha")) {
            this.asesorFicha = asesor;
        }
    }

    // ─── Factory: build ───────────────────────────────────────────────────────

    public static FichaPerfil build(String titulo, AsesorFicha asesor) {
        FichaPerfil ficha = new FichaPerfil();
        ValidationResult result = new ValidationResult();

        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFicha(asesor, result);

        result.throwIfHasErrors();  // lanza DomainValidationException con TODOS los errores

        ficha.setId();              // UUID solo se genera si la validación pasa
        return ficha;
    }

    // ─── Factory: rebuild (desde persistencia — dato confiable) ──────────────

    public static FichaPerfil rebuild(UUID id, String titulo, AsesorFicha asesor) {
        FichaPerfil ficha = new FichaPerfil();
        ficha.id = id;
        ficha.tituloProyecto = titulo;
        ficha.asesorFicha = asesor;
        return ficha;
    }

    // ─── Métodos de negocio ───────────────────────────────────────────────────

    public void actualizarTitulo(String nuevoTitulo) {
        ValidationResult result = new ValidationResult();
        setTituloProyecto(nuevoTitulo, result);
        result.throwIfHasErrors();
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

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
