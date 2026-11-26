package com.arquisoft.shared.catalogoparametros.infrastructure.primaryadapter.scheduler;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.RefrescarParametrosCatalogoInteractor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@CatalogoParametrosEncendido
@RequiredArgsConstructor
public class RefrescarParametrosCatalogoScheduler {

    private final RefrescarParametrosCatalogoInteractor refrescarParametrosCatalogoInteractor;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;

    @Scheduled(
            initialDelayString = "${catalogo.parametros.refresh-interval:PT5M}",
            fixedDelayString = "${catalogo.parametros.refresh-interval:PT5M}")
    public void refrescar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            try {
                refrescarParametrosCatalogoInteractor.ejecutar();
            } catch (RuntimeException e) {
                logger.error(CatalogoParametrosKey.LOG_REFRESCO_INESPERADO, e.getClass().getSimpleName());
            }
        }
    }
}
