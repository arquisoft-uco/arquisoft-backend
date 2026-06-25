package com.arquisoft.fichas.domain.estadofichaperfil.aggregate;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilDate;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EstadoFichaPerfilAggregate {

    private final UUID id;
    private final UUID fichaPerfilId;
    private final EstadoFicha estadoFicha;
    private final Instant fechaActualizacion;

    private EstadoFichaPerfilAggregate(UUID id, UUID fichaPerfilId, EstadoFicha estadoFicha,
                                       Instant fechaActualizacion) {
        this.id = id;
        this.fichaPerfilId = fichaPerfilId;
        this.estadoFicha = estadoFicha;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static EstadoFichaPerfilAggregate crear(UUID fichaPerfilId) {
        var result = new ValidationResult();

        DomainValidator.notNull(fichaPerfilId,
                FichasMessages.EstadoFichaPerfil.CAMPO_FICHA_PERFIL_ID,
                FichasMessages.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO,
                result);

        result.throwIfHasErrors();

        return new EstadoFichaPerfilAggregate(
                UUID.randomUUID(),
                fichaPerfilId,
                EstadoFicha.EN_CONSTRUCCION,
                UtilDate.generateNewInstantNow()
        );
    }

    public static EstadoFichaPerfilAggregate reconstruir(UUID id, UUID fichaPerfilId,
                                                         EstadoFicha estadoFicha,
                                                         Instant fechaActualizacion) {
        return new EstadoFichaPerfilAggregate(id, fichaPerfilId, estadoFicha, fechaActualizacion);
    }

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
