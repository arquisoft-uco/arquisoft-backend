package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilMapper {

    private final EstadoFichaJpaRepository estadoFichaJpaRepository;

    public EstadoFichaPerfilJpaEntity toJpaEntity(EstadoFichaPerfilAggregate aggregate) {
        var estadoFichaEntity = estadoFichaJpaRepository.findByNombre(
                aggregate.getEstadoFicha().getNombre()
        ).orElseThrow(() -> new IllegalStateException(
                "Estado no encontrado en catálogo: " + aggregate.getEstadoFicha().getNombre()
        ));

        return EstadoFichaPerfilJpaEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .estadoFichaId(estadoFichaEntity.getId())
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public EstadoFichaPerfilAggregate toDomain(EstadoFichaPerfilJpaEntity entity) {
        var estadoFichaEntity = estadoFichaJpaRepository.findById(entity.getEstadoFichaId())
                .orElseThrow(() -> new IllegalStateException(
                        "Estado no encontrado en catálogo: " + entity.getEstadoFichaId()
                ));

        EstadoFicha estadoFicha = null;
        for (EstadoFicha ef : EstadoFicha.values()) {
            if (ef.getNombre().equals(estadoFichaEntity.getNombre())) {
                estadoFicha = ef;
                break;
            }
        }

        if (estadoFicha == null) {
            throw new IllegalStateException(
                    "No se pudo mapear el nombre del catálogo a enum: " + estadoFichaEntity.getNombre()
            );
        }

        return EstadoFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                estadoFicha,
                entity.getFechaActualizacion()
        );
    }
}
