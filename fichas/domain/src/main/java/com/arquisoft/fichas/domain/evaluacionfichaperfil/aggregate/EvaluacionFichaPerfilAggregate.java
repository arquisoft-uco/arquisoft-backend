package com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate;

import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilDate;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EvaluacionFichaPerfilAggregate {

    private UUID id;
    private UUID representanteComiteId;
    private UUID fichaPerfilId;
    private Instant fechaCreacion;

    private EvaluacionFichaPerfilAggregate() {}

    private EvaluacionFichaPerfilAggregate(UUID id, UUID representanteComiteId, UUID fichaPerfilId, Instant fechaCreacion) {
        this.id = id;
        this.representanteComiteId = representanteComiteId;
        this.fichaPerfilId = fichaPerfilId;
        this.fechaCreacion = fechaCreacion;
    }

    public static EvaluacionFichaPerfilAggregate crear(UUID representanteComiteId, UUID fichaPerfilId) {
        var evaluacion = new EvaluacionFichaPerfilAggregate();
        var result = new ValidationResult();

        evaluacion.setId();
        evaluacion.setRepresentanteComiteId(representanteComiteId, result);
        evaluacion.setFichaPerfilId(fichaPerfilId, result);
        evaluacion.setFechaCreacion();

        result.lanzarSiTieneErrores();
        return evaluacion;
    }

    public static EvaluacionFichaPerfilAggregate reconstruir(UUID id, UUID representanteComiteId, UUID fichaPerfilId,
            Instant fechaCreacion) {
        return new EvaluacionFichaPerfilAggregate(id, representanteComiteId, fichaPerfilId, fechaCreacion);
    }

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setRepresentanteComiteId(UUID representanteComiteId, ValidationResult result) {
        if (!DomainValidator.noNulo(
                representanteComiteId,
                FichasMessages.EvaluacionFichaPerfil.CAMPO_REPRESENTANTE_COMITE,
                FichasMessages.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO,
                result)) {
            return;
        }
        this.representanteComiteId = representanteComiteId;
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(
                fichaPerfilId,
                FichasMessages.EvaluacionFichaPerfil.CAMPO_FICHA_PERFIL,
                FichasMessages.EvaluacionFichaPerfil.FICHA_REQUERIDA,
                result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilDate.generateNewInstantNow();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepresentanteComiteId() {
        return representanteComiteId;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }
}
