package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
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

        result.lanzarSiTieneErrores();
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
        result.lanzarSiTieneErrores();
    }

    public void cambiarAsesorFicha(UUID nuevoAsesorFichaId, EstadoFicha estadoActual) {
        var result = new ValidationResult();

        DomainValidator.noNulo(nuevoAsesorFichaId,
                FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.ASESOR_REQUERIDO,
                result);

        if (!UtilObject.isNull(nuevoAsesorFichaId) && nuevoAsesorFichaId.equals(this.asesorFichaId)) {
            result.agregarError(
                    FichasFields.FichaPerfil.ASESOR_FICHA,
                    FichasCodes.FichaPerfil.MISMO_ASESOR,
                    Messages.formatear(FichasKeys.FichaPerfil.ERROR_MISMO_ASESOR, nuevoAsesorFichaId)
            );
        }

        if (estadoActual.esTerminal()) {
            result.agregarError(
                    FichasFields.FichaPerfil.ESTADO_FICHA,
                    FichasCodes.FichaPerfil.ESTADO_TERMINAL,
                    Messages.formatear(FichasKeys.FichaPerfil.ERROR_ESTADO_TERMINAL, estadoActual)
            );
        }

        result.lanzarSiTieneErrores();

        this.asesorFichaId = nuevoAsesorFichaId;
    }

    // ─── Private setters ──────────────────────────────────────────────────────

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(titulo,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(titulo, FichasLimits.FichaPerfil.TITULO_MAX,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tituloProyecto = UtilText.applyTrim(titulo);
    }

    private void setAsesorFichaId(UUID asesorFichaId, ValidationResult result) {
        if (!DomainValidator.noNulo(asesorFichaId,
                FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
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
