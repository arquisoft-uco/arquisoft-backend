package com.arquisoft.fichas.domain.model;

import com.arquisoft.fichas.domain.utils.messages.FichasMessages;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.util.UtilText;
import com.arquisoft.shared.validation.util.UtilUUID;

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
 *       (Notification Pattern) y lanza {@link com.arquisoft.shared.validation.exception.DomainValidationException}
 *       con la lista completa si existe al menos uno. Genera UUID solo si la validación pasa.</li>
 *   <li>{@link #rebuild(UUID, String, AsesorFicha)} — reconstruye desde persistencia sin validación.</li>
 * </ul>
 */
public final class FichaPerfil {

    private UUID id;
    private String tituloProyecto;
    private AsesorFicha asesorFicha;

    private FichaPerfil() {}

    private FichaPerfil(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFicha = asesorFicha;
    }

    // ─── Factory: build ───────────────────────────────────────────────────────

    public static FichaPerfil build(String titulo, AsesorFicha asesor) {
        FichaPerfil ficha = new FichaPerfil();
        ValidationResult result = new ValidationResult();  // una instancia por llamada

        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFicha(asesor, result);

        result.throwIfHasErrors();  // lanza con TODOS los errores acumulados

        ficha.setId();
        return ficha;
    }

    // ─── Factory: rebuild (desde persistencia — dato confiable) ──────────────

    public static FichaPerfil rebuild(UUID id, String titulo, AsesorFicha asesor) {
        return new FichaPerfil(id, titulo, asesor);
    }

    // ─── Métodos de negocio ───────────────────────────────────────────────────

    public void actualizarTitulo(String nuevoTitulo) {
        ValidationResult result = new ValidationResult();
        setTituloProyecto(nuevoTitulo, result);
        result.throwIfHasErrors();
    }

    // ─── Private setters ──────────────────────────────────────────────────────
    // Cada setter delega sus reglas en DomainValidator, acumula en result
    // y solo asigna el valor si ese campo específico no presentó errores.
    // Son la única fuente de verdad por campo: reutilizables desde build()
    // y cualquier método de mutación de negocio.

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        DomainValidator.notBlank(titulo, FichasMessages.FichaPerfil.CAMPO_TITULO, FichasMessages.FichaPerfil.TITULO_REQUERIDO, result);
        DomainValidator.maxLength(titulo, FichasMessages.FichaPerfil.TITULO_MAX,
                FichasMessages.FichaPerfil.CAMPO_TITULO, FichasMessages.FichaPerfil.TITULO_DEMASIADO_LARGO, result);
        if (!result.hasFieldErrors(FichasMessages.FichaPerfil.CAMPO_TITULO)) {
            this.tituloProyecto = UtilText.applyTrim(titulo);
        }
    }

    private void setAsesorFicha(AsesorFicha asesor, ValidationResult result) {
        DomainValidator.notNull(asesor, FichasMessages.FichaPerfil.CAMPO_ASESOR, FichasMessages.FichaPerfil.ASESOR_REQUERIDO, result);
        if (!result.hasFieldErrors(FichasMessages.FichaPerfil.CAMPO_ASESOR)) {
            this.asesorFicha = asesor;
        }
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
