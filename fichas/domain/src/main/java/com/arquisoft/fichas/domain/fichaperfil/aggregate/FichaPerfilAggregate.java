package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilObject;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.util.UtilUUID;
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
        var ficha = new FichaPerfilAggregate();
        var result = new ValidationResult();

        ficha.setId();
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

    public void cambiarAsesorFicha(UUID nuevoAsesorFichaId, EstadoFicha estadoActual) {
        var result = new ValidationResult();

        DomainValidator.notNull(nuevoAsesorFichaId,
                FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA,
                FichasMessages.FichaPerfil.ASESOR_REQUERIDO,
                result);

        if (!UtilObject.isNull(nuevoAsesorFichaId) && nuevoAsesorFichaId.equals(this.asesorFichaId)) {
            result.addError(
                    FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA,
                    FichasMessages.FichaPerfil.MISMO_ASESOR,
                    FichasMessages.FichaPerfil.MISMO_ASESOR_MSG.formatted(nuevoAsesorFichaId)
            );
        }

        if (estadoActual.esTerminal()) {
            result.addError(
                    FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA,
                    FichasMessages.FichaPerfil.ESTADO_TERMINAL,
                    FichasMessages.FichaPerfil.ESTADO_TERMINAL_MSG.formatted(estadoActual)
            );
        }

        result.throwIfHasErrors();

        this.asesorFichaId = nuevoAsesorFichaId;
    }

    // ─── Private setters ──────────────────────────────────────────────────────

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
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
                FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA,
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
