package com.arquisoft.notificaciones.application.notificacion.command.finder;

import com.arquisoft.notificaciones.application.notificacion.command.finder.model.CriterioReintento;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;

public interface NotificacionesReintentablesFinder
        extends Finder<CriterioReintento, List<NotificacionDomain>> {
}
