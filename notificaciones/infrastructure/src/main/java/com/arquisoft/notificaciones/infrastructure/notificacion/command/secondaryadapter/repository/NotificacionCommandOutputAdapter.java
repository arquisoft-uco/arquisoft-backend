package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.mapper.NotificacionJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.NotificacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionCommandOutputAdapter implements NotificacionOutputPort {

    private final NotificacionCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void guardar(NotificacionEntity notificacion) {
        repository.save(NotificacionJpaMapper.toJpaEntity(notificacion));
        logger.debug(Mensajes.obtener(NotificacionKey.LOG_GUARDADA),
                notificacion.idEvento(), notificacion.estado());
    }

    @Override
    public boolean existePorIdEvento(String idEvento) {
        return repository.existsByIdEvento(idEvento);
    }
}
