package com.arquisoft.fichas.domain.evaluacionfichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EvaluacionFichaPerfilDomain {

    private UUID id;
    private UUID representanteComiteId;
    private UUID fichaPerfilId;
    private Instant fechaCreacion;

    private EvaluacionFichaPerfilDomain() {}

    private EvaluacionFichaPerfilDomain(UUID id, UUID representanteComiteId, UUID fichaPerfilId, Instant fechaCreacion) {
        this.id = id;
        this.representanteComiteId = representanteComiteId;
        this.fichaPerfilId = fichaPerfilId;
        this.fechaCreacion = fechaCreacion;
    }

    public static EvaluacionFichaPerfilDomain crear(UUID representanteComiteId, UUID fichaPerfilId) {
        var evaluacion = new EvaluacionFichaPerfilDomain();
        var result = new ValidationResult();

        evaluacion.setId();
        evaluacion.setRepresentanteComiteId(representanteComiteId, result);
        evaluacion.setFichaPerfilId(fichaPerfilId, result);
        evaluacion.setFechaCreacion();

        result.lanzarSiTieneErrores();
        return evaluacion;
    }

    public static EvaluacionFichaPerfilDomain reconstruir(UUID id, UUID representanteComiteId, UUID fichaPerfilId,
            Instant fechaCreacion) {
        return new EvaluacionFichaPerfilDomain(id, representanteComiteId, fichaPerfilId, fechaCreacion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setRepresentanteComiteId(UUID representanteComiteId, ValidationResult result) {
        if (!DomainValidator.noNulo(
                representanteComiteId,
                FichasFields.EvaluacionFichaPerfil.REPRESENTANTE_COMITE,
                FichasCodes.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO,
                result)) {
            return;
        }
        this.representanteComiteId = representanteComiteId;
    }

    private void setFichaPerfilId(UUID fichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(
                fichaPerfilId,
                FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL,
                FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA,
                result)) {
            return;
        }
        this.fichaPerfilId = fichaPerfilId;
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarInstanteActual();
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
