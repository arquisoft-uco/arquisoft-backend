package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import org.springframework.stereotype.Component;

@Component
public class EstadoFichaMapper {

    public EstadoFichaReadModel toReadModel(EstadoFichaJpaEntity entity) {
        return new EstadoFichaReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }
}
