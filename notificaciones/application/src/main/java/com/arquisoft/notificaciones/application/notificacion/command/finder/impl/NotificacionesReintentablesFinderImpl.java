package com.arquisoft.notificaciones.application.notificacion.command.finder.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionesReintentablesFinder;
import com.arquisoft.notificaciones.application.notificacion.command.finder.model.CriterioReintento;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.mapper.NotificacionMapper;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificacionesReintentablesFinderImpl implements NotificacionesReintentablesFinder {

    private final NotificacionOutputPort notificacionOutputPort;

    @Override
    public List<NotificacionDomain> obtener(CriterioReintento entrada) {
        return notificacionOutputPort
                .buscarFallidasReintentables(entrada.maxIntentos(), entrada.limite())
                .stream()
                .map(NotificacionMapper::toDomain)
                .toList();
    }
}
