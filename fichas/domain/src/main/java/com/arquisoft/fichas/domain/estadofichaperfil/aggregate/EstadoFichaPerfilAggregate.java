package com.arquisoft.fichas.domain.estadofichaperfil.aggregate;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilDate;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EstadoFichaPerfilAggregate {

    private UUID id;
    private UUID fichaPerfilId;
    private EstadoFicha estadoFicha;
    private Instant fechaActualizacion;

    private EstadoFichaPerfilAggregate() {}

    private EstadoFichaPerfilAggregate(UUID id, UUID fichaPerfilId, EstadoFicha estadoFicha, Instant fechaActualizacion) {
        this.id = id;
        this.fichaPerfilId = fichaPerfilId;
        this.estadoFicha = estadoFicha;
        this.fechaActualizacion = fechaActualizacion;
    }
// ─── Factory: crear (entidad nueva — valida invariantes) ─────────────────

    public static EstadoFichaPerfilAggregate crear(UUID fichaPerfilId) {
        var aggregate = new EstadoFichaPerfilAggregate();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setFichaPerfilId(fichaPerfilId, result);
        aggregate.setEstadoFichaInicial();
        aggregate.setFechaActualizacion();

        result.throwIfHasErrors();
        return aggregate;
    }

    // ─── Factory: reconstruir (desde persistencia — dato confiable) ──────────

    public static EstadoFichaPerfilAggregate reconstruir(UUID id, UUID fichaPerfilId,
                                                         EstadoFicha estadoFicha,
                                                         Instant fechaActualizacion) {
        return new EstadoFichaPerfilAggregate(id, fichaPerfilId, estadoFicha, fechaActualizacion);
    }

    // ─── Private setters ──────────────────────────────────────────────────────

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.notNull(fichaPerfilId,
                FichasMessages.EstadoFichaPerfil.CAMPO_FICHA_PERFIL_ID,
                FichasMessages.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setEstadoFichaInicial() {
        this.estadoFicha = EstadoFicha.EN_CONSTRUCCION;
    }

    private void setFechaActualizacion() {
        this.fechaActualizacion = UtilDate.generateNewInstantNow();
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public EstadoFicha getEstadoFicha() {
        return estadoFicha;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}
