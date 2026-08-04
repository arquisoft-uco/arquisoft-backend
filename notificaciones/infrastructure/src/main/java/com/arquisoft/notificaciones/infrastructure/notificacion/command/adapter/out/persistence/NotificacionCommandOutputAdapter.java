package com.arquisoft.notificaciones.infrastructure.notificacion.command.adapter.out.persistence;

import com.arquisoft.notificaciones.domain.notificacion.aggregate.NotificacionAggregate;
import com.arquisoft.notificaciones.domain.notificacion.port.out.NotificacionOutputPort;
import com.arquisoft.notificaciones.infrastructure.notificacion.persistence.NotificacionMapper;
import com.arquisoft.notificaciones.infrastructure.notificacion.persistence.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionCommandOutputAdapter implements NotificacionOutputPort {

    private final NotificacionRepository repository;

    @Override
    public void guardar(NotificacionAggregate notificacion) {
        repository.save(NotificacionMapper.toEntity(notificacion));
    }

    @Override
    public boolean existePorEventId(String eventId) {
        return repository.existsByEventId(eventId);
    }
}
