package com.arquisoft.shared.catalogoparametros.infrastructure.primaryadapter.arranque;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.RefrescarParametrosCatalogoInteractor;
import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.ValidadorConfiguracionCatalogoParametros;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.CargaInicialParametrosCatalogoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@CatalogoParametrosEncendido
@DependsOn("catalogoMensajesRedis")
@RequiredArgsConstructor
public class CargaInicialParametrosCatalogo {

    private static final String MOTIVO_ESTADO_PREFIJO = "estado-";
    private static final String MOTIVO_SUSPENDIDO = "suspendido";
    private static final String MOTIVO_SIN_CONFIGURAR = "sin-configurar";

    // Se inyecta solo para que la configuracion se valide antes de la carga: el bean escaneado se
    // nombra por FQN, asi que un @DependsOn por cadena no lo encontraria.
    private final ValidadorConfiguracionCatalogoParametros validadorConfiguracion;
    private final RefrescarParametrosCatalogoInteractor refrescarParametrosCatalogoInteractor;
    private final CatalogoParametrosProperties propiedades;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;

    private int cantidadCargada;

    @PostConstruct
    public void cargar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            var resultado = refrescar();
            switch (resultado) {
                case RefrescoParametrosCatalogoResult.Refrescado refrescado -> cantidadCargada = refrescado.cantidad();
                case RefrescoParametrosCatalogoResult.Conservado conservado -> throw abortar(conservado.motivo());
                case RefrescoParametrosCatalogoResult.Rechazado rechazado ->
                        throw abortar(MOTIVO_ESTADO_PREFIJO + rechazado.estado());
                case RefrescoParametrosCatalogoResult.Suspendido suspendido -> throw abortar(MOTIVO_SUSPENDIDO);
                case RefrescoParametrosCatalogoResult.SinConfigurar sinConfigurar ->
                        throw abortar(MOTIVO_SIN_CONFIGURAR);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registrarCarga() {
        logger.info(CatalogoParametrosKey.LOG_CARGA_INICIAL, cantidadCargada);
        logger.info(CatalogoParametrosKey.LOG_CONFIGURACION, propiedades.origenBaseUrl(),
                propiedades.refreshInterval(), propiedades.defaults().size());
    }

    // La excepcion original no se encadena como causa: su mensaje puede incluir la URL con el
    // tokenId o la cabecera con el secreto, y Spring imprime la cadena completa al abortar.
    private RefrescoParametrosCatalogoResult refrescar() {
        try {
            return refrescarParametrosCatalogoInteractor.ejecutar();
        } catch (RuntimeException e) {
            throw abortar(e.getClass().getSimpleName());
        }
    }

    private CargaInicialParametrosCatalogoException abortar(String motivo) {
        logger.error(CatalogoParametrosKey.LOG_CARGA_INICIAL_FALLIDA, motivo);
        return new CargaInicialParametrosCatalogoException(motivo);
    }
}
