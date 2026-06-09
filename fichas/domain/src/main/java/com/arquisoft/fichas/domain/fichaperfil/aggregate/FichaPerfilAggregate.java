package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class FichaPerfilAggregate {

    private UUID id;
    private String tituloProyecto;
    private UUID asesorFichaId;

    private FichaPerfilAggregate() {}

    private FichaPerfilAggregate(UUID id, String tituloProyecto, UUID asesorFichaId) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFichaId = asesorFichaId;
    }

    // ─── Factory: crear (entidad nueva — valida invariantes) ─────────────────

    public static FichaPerfilAggregate crear(String titulo, UUID asesorFichaId) {
        FichaPerfilAggregate ficha = new FichaPerfilAggregate();
        ValidationResult result = new ValidationResult();

        ficha.setId(UUID.randomUUID(), result);
        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFichaId(asesorFichaId, result);

        result.throwIfHasErrors();
        return ficha;
    }

    // ─── Factory: reconstruir (desde persistencia — dato confiable) ──────────

    public static FichaPerfilAggregate reconstruir(UUID id, String titulo, UUID asesorFichaId) {
        return new FichaPerfilAggregate(id, titulo, asesorFichaId);
    }

    // ─── Métodos de negocio ───────────────────────────────────────────────────

    public void actualizarTitulo(String nuevoTitulo) {
        ValidationResult result = new ValidationResult();
        setTituloProyecto(nuevoTitulo, result);
        result.throwIfHasErrors();
    }

    // ─── Private setters ──────────────────────────────────────────────────────

    private void setId(UUID id, ValidationResult result) {
        if (!DomainValidator.notNull(id,
                FichasMessages.FichaPerfil.CAMPO_ID,
                FichasMessages.FichaPerfil.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        if (!DomainValidator.notBlank(titulo,
                FichasMessages.FichaPerfil.CAMPO_TITULO,
                FichasMessages.FichaPerfil.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.maxLength(titulo, FichasMessages.FichaPerfil.TITULO_MAX,
                FichasMessages.FichaPerfil.CAMPO_TITULO,
                FichasMessages.FichaPerfil.TITULO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tituloProyecto = UtilText.applyTrim(titulo);
    }

    private void setAsesorFichaId(UUID asesorFichaId, ValidationResult result) {
        if (!DomainValidator.notNull(asesorFichaId,
                FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA_ID,
                FichasMessages.FichaPerfil.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesorFichaId = asesorFichaId;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getAsesorFichaId() {
        return asesorFichaId;
    }
}
