package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionCommandOutputAdapter implements NotificacionOutputPort {

    private final NotificacionCommandRepository repository;

    @Override
    public void guardar(NotificacionEntity notificacion) {
        repository.save(notificacion);
    }

    @Override
    public boolean existePorIdEvento(String idEvento) {
        return repository.existsByIdEvento(idEvento);
    }
}
