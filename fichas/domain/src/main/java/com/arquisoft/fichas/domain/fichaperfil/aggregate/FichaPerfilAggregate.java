package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.fichas.domain.fichaperfil.message.FichaPerfilMessages;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.util.UtilText;

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

    // ─── Factory: build ───────────────────────────────────────────────────────

    public static FichaPerfilAggregate build(UUID id, String titulo, UUID asesorFichaId) {
        FichaPerfilAggregate ficha = new FichaPerfilAggregate();
        ValidationResult result = new ValidationResult();

        ficha.setId(id, result);
        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFichaId(asesorFichaId, result);

        result.throwIfHasErrors();
        return ficha;
    }

    // ─── Factory: rebuild (desde persistencia — dato confiable) ──────────────

    public static FichaPerfilAggregate rebuild(UUID id, String titulo, UUID asesorFichaId) {
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
                FichaPerfilMessages.CAMPO_ID,
                FichaPerfilMessages.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        if (!DomainValidator.notBlank(titulo,
                FichaPerfilMessages.CAMPO_TITULO,
                FichaPerfilMessages.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.maxLength(titulo, FichaPerfilMessages.TITULO_MAX,
                FichaPerfilMessages.CAMPO_TITULO,
                FichaPerfilMessages.TITULO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tituloProyecto = UtilText.applyTrim(titulo);
    }

    private void setAsesorFichaId(UUID asesorFichaId, ValidationResult result) {
        if (!DomainValidator.notNull(asesorFichaId,
                FichaPerfilMessages.CAMPO_ASESOR_FICHA_ID,
                FichaPerfilMessages.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesorFichaId = asesorFichaId;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public String getTituloProyecto() { return tituloProyecto; }
    public UUID getAsesorFichaId() { return asesorFichaId; }
}
