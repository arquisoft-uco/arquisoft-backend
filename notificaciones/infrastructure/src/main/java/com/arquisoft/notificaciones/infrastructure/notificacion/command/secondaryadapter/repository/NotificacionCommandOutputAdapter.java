package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.infrastructure.notificacion.persistence.NotificacionMapper;
import com.arquisoft.notificaciones.infrastructure.notificacion.persistence.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionCommandOutputAdapter implements NotificacionOutputPort {

    private final NotificacionRepository repository;

    @Override
    public void guardar(NotificacionDomain notificacion) {
        repository.save(NotificacionMapper.toEntity(notificacion));
    }

    @Override
    public boolean existePorIdEvento(String idEvento) {
        return repository.existsByIdEvento(idEvento);
    }
}
