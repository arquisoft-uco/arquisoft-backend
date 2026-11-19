package com.arquisoft.shared.redis.catalogo.messageuco;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.messageuco.ProcesadorAvisosMessageUco.AvisoMessageUco;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.context.SmartLifecycle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Escucha los avisos de cambio que MessageUcoLab publica en Pulsar y los aplica al catálogo (ADR-017).
 *
 * <p>Con esto el catálogo solo se recarga en dos momentos: completo al arrancar y, después, cada vez
 * que llegan avisos. No hay recarga periódica.
 *
 * <ul>
 *   <li><b>Suscripción durable y compartida por nombre.</b> Si el backend está caído, Pulsar guarda
 *       los avisos. Todas las instancias usan el mismo nombre, así que cada aviso lo procesa una
 *       sola: Redis es común, no hace falta que lo escriban todas.</li>
 *   <li><b>Failover.</b> Una instancia activa por vez: los avisos se aplican en el orden en que
 *       se publicaron.</li>
 *   <li><b>Sin avisos viejos al arrancar.</b> La suscripción arranca en pausa y salta al instante en
 *       que empezó la sincronización completa del arranque: lo publicado antes ya está en Redis, y
 *       repetirlo sería una consulta por mensaje. Si esa sincronización falló, no se salta nada y los
 *       avisos guardados se aplican.</li>
 *   <li><b>Por lotes.</b> Los avisos que llegan dentro de {@code ventanaLote} se aplican juntos; un
 *       lote grande, como el de una carga masiva, se resuelve con una sola consulta de todo.</li>
 *   <li><b>Pulsar caído al arrancar no impide arrancar.</b> La suscripción se reintenta cada
 *       {@code reintentoSuscripcion}; una vez suscrito, el cliente de Pulsar se reconecta solo.</li>
 *   <li><b>Un lote que falla por MessageUcoLab se vuelve a entregar</b> tras {@code reintentoAviso};
 *       un aviso que nunca va a funcionar (JSON inválido) se descarta con un log.</li>
 * </ul>
 */
public class EscuchaAvisosMessageUco implements SmartLifecycle {

    private static final String LOG_SUSCRITO = "Escuchando avisos de MessageUcoLab en {} (suscripción {})";
    private static final String LOG_DESDE = "Avisos de MessageUcoLab anteriores a {} omitidos: la sincronización del arranque ya los cubrió";
    private static final String LOG_SIN_SALTO =
            "La sincronización del arranque no terminó: se aplican también los avisos guardados en Pulsar";
    private static final String LOG_FALLO_SALTO =
            "No se pudieron omitir los avisos anteriores al arranque, se aplican todos: {}";
    private static final String LOG_FALLO_SUSCRIPCION =
            "No se pudo suscribir a los avisos de MessageUcoLab en {}, se reintenta en {}: {}";
    private static final String LOG_REINTENTO =
            "No se pudo aplicar el lote de {} aviso(s) de MessageUcoLab, se vuelve a entregar en {}: {}";
    private static final String LOG_DESCARTADO = "Aviso de MessageUcoLab descartado, no se puede aplicar: {}";
    private static final String LOG_FALLO_CIERRE = "No se pudo cerrar la conexión con Pulsar: {}";
    private static final String MENSAJE_URL_INVALIDA = "Dirección de Pulsar inválida: ";
    private static final String NOMBRE_HILO = "messageuco-avisos-";

    private final ProcesadorAvisosMessageUco procesador;
    private final SincronizadorCatalogoMessageUco sincronizador;
    private final MessageUcoProperties.Eventos eventos;
    private final String topic;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;

    private final List<Pendiente> pendientes = new ArrayList<>();
    private boolean lotePendiente;

    private ScheduledExecutorService tareas;
    private PulsarClient cliente;
    private volatile Consumer<String> consumidor;
    private volatile boolean activo;

    public EscuchaAvisosMessageUco(
            ProcesadorAvisosMessageUco procesador,
            SincronizadorCatalogoMessageUco sincronizador,
            MessageUcoProperties.Eventos eventos,
            String clientId,
            GestorTraza gestorTraza,
            AppLogger logger) {
        this.procesador = procesador;
        this.sincronizador = sincronizador;
        this.eventos = eventos;
        this.topic = eventos.prefijoTopic() + "-" + clientId;
        this.gestorTraza = gestorTraza;
        this.logger = logger;
    }

    @Override
    public void start() {
        try {
            cliente = PulsarClient.builder().serviceUrl(eventos.url()).build();
        } catch (PulsarClientException e) {
            throw new IllegalStateException(MENSAJE_URL_INVALIDA + eventos.url(), e);
        }
        tareas = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().name(NOMBRE_HILO, 0).factory());
        activo = true;
        suscribir();
    }

    @Override
    public void stop() {
        activo = false;
        if (UtilObjeto.noEsNulo(tareas)) {
            tareas.shutdownNow();
        }
        cerrar(consumidor);
        cerrar(cliente);
    }

    @Override
    public boolean isRunning() {
        return activo;
    }

    String topic() {
        return topic;
    }

    private void suscribir() {
        if (!activo) {
            return;
        }
        cliente.newConsumer(Schema.STRING)
                .topic(topic)
                .subscriptionName(eventos.suscripcion())
                .subscriptionType(SubscriptionType.Failover)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                // En pausa hasta decidir desde dónde leer: si no, los avisos viejos empiezan a
                // llegar antes de que el salto se aplique.
                .startPaused(true)
                .negativeAckRedeliveryDelay(eventos.reintentoAviso().toMillis(), TimeUnit.MILLISECONDS)
                .messageListener(this::recibir)
                .subscribeAsync()
                .whenComplete((suscrito, error) -> {
                    if (UtilObjeto.noEsNulo(error)) {
                        logger.warn(LOG_FALLO_SUSCRIPCION, topic, eventos.reintentoSuscripcion(), error.getMessage());
                        programar(this::suscribir, eventos.reintentoSuscripcion().toMillis());
                    } else {
                        consumidor = suscrito;
                        logger.info(LOG_SUSCRITO, topic, eventos.suscripcion());
                        empezarALeer(suscrito);
                    }
                });
    }

    private void empezarALeer(Consumer<String> suscrito) {
        Optional<Instant> desde = sincronizador.inicioUltimaSincronizacionCompleta();
        if (desde.isEmpty()) {
            logger.warn(LOG_SIN_SALTO);
            suscrito.resume();
            return;
        }
        suscrito.seekAsync(desde.get().toEpochMilli()).whenComplete((nada, error) -> {
            if (UtilObjeto.noEsNulo(error)) {
                logger.warn(LOG_FALLO_SALTO, error.getMessage());
            } else {
                logger.info(LOG_DESDE, desde.get());
            }
            suscrito.resume();
        });
    }

    /**
     * Corre en el hilo de listener de Pulsar. Solo encola: el lote se aplica en el hilo propio de
     * esta clase cuando vence la ventana.
     */
    void recibir(Consumer<String> origen, Message<String> aviso) {
        synchronized (pendientes) {
            pendientes.add(new Pendiente(origen, aviso));
            if (lotePendiente) {
                return;
            }
            lotePendiente = true;
        }
        programar(this::aplicarLote, eventos.ventanaLote().toMillis());
    }

    void aplicarLote() {
        List<Pendiente> lote;
        synchronized (pendientes) {
            lote = List.copyOf(pendientes);
            pendientes.clear();
            lotePendiente = false;
        }
        if (lote.isEmpty()) {
            return;
        }

        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraEvento(UtilTexto.VACIO, topic, UtilTexto.VACIO))) {
            List<Pendiente> leidos = new ArrayList<>();
            List<AvisoMessageUco> avisos = new ArrayList<>();
            for (Pendiente pendiente : lote) {
                try {
                    avisos.add(procesador.leer(pendiente.aviso().getValue()));
                    leidos.add(pendiente);
                } catch (RuntimeException e) {
                    logger.error(LOG_DESCARTADO, e.getMessage());
                    pendiente.confirmar();
                }
            }
            if (avisos.isEmpty()) {
                return;
            }

            try {
                procesador.aplicar(avisos);
                leidos.forEach(Pendiente::confirmar);
            } catch (RuntimeException e) {
                if (ProcesadorAvisosMessageUco.esTransitorio(e)) {
                    logger.warn(LOG_REINTENTO, leidos.size(), eventos.reintentoAviso(), e.getMessage());
                    leidos.forEach(Pendiente::reintentar);
                } else {
                    logger.error(LOG_DESCARTADO, e.getMessage());
                    leidos.forEach(Pendiente::confirmar);
                }
            }
        }
    }

    private void programar(Runnable tarea, long esperaMilisegundos) {
        if (activo) {
            tareas.schedule(tarea, esperaMilisegundos, TimeUnit.MILLISECONDS);
        }
    }

    private void cerrar(AutoCloseable recurso) {
        if (UtilObjeto.esNulo(recurso)) {
            return;
        }
        try {
            recurso.close();
        } catch (Exception e) {
            logger.warn(LOG_FALLO_CIERRE, e.getMessage());
        }
    }

    private record Pendiente(Consumer<String> origen, Message<String> aviso) {

        void confirmar() {
            origen.acknowledgeAsync(aviso);
        }

        void reintentar() {
            origen.negativeAcknowledge(aviso);
        }
    }
}
