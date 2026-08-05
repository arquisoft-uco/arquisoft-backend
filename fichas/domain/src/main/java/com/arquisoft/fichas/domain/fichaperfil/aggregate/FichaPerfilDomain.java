package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class FichaPerfilDomain extends AggregateRoot {

    private UUID id;
    private String tituloProyecto;
    private UUID asesorFicha;

    private FichaPerfilDomain() {}

    private FichaPerfilDomain(UUID id, String tituloProyecto, UUID asesorFicha) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFicha = asesorFicha;
    }

    // ─── Factory: crear (entidad nueva — valida invariantes) ─────────────────

    public static FichaPerfilDomain crear(String titulo, UUID asesorFicha) {
        var ficha = new FichaPerfilDomain();
        var result = new ValidationResult();

        ficha.setId();
        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return ficha;
    }

    // ─── Factory: reconstruir (desde persistencia — dato confiable) ──────────

    public static FichaPerfilDomain reconstruir(UUID id, String titulo, UUID asesorFichaId) {
        return new FichaPerfilDomain(id, titulo, asesorFichaId);
    }

    // ─── Métodos de negocio ───────────────────────────────────────────────────

    public void actualizarTitulo(String nuevoTitulo) {
        var result = new ValidationResult();
        setTituloProyecto(nuevoTitulo, result);
        result.lanzarSiTieneErrores();
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

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!DomainValidator.noNulo(asesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }
}
