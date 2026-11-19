package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.ReanudarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.ReanudarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
import com.arquisoft.proyectos.infrastructure.config.ProyectosMapasRutaQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import com.arquisoft.shared.util.UtilFecha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.ZoneId;

@Component
public class MapaRutaFechaFinCambiadaConsumer extends AbstractEventConsumer {

    private final ReanudarProyectoGradoInteractor reanudarProyectoGradoInteractor;
    private final AppLogger logger;
    private final ZoneId zona;

    public MapaRutaFechaFinCambiadaConsumer(
            ReanudarProyectoGradoInteractor reanudarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza,
            @Value("${plazo-mapa-ruta.zona-horaria:America/Bogota}") String zona) {
        super(objectMapper, gestorTraza, logger);
        this.reanudarProyectoGradoInteractor = reanudarProyectoGradoInteractor;
        this.logger = logger;
        this.zona = ZoneId.of(zona);
    }

    @RabbitListener(queues = ProyectosMapasRutaQueueConfig.MAPA_RUTA_FECHA_FIN_CAMBIADA_QUEUE)
    public void onMapaRutaFechaFinCambiada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, MapaRutaFechaFinCambiadaPayload.class);
            logger.info(ProyectoGradoKey.LOG_FECHA_FIN_CAMBIADA_RECIBIDA,
                    payload.idEvento(), payload.mapaRuta(), payload.proyectoGrado(), payload.fechaFin());

            var resultado = reanudarProyectoGradoInteractor.ejecutar(ReanudarProyectoGradoCommand.crear(
                    payload.proyectoGrado(), payload.fechaFin(), UtilFecha.generarFechaActual(zona)));

            switch (resultado) {
                case ReanudacionProyectoGradoResult.Reanudado reanudado ->
                        logger.info(ProyectoGradoKey.LOG_REANUDADO, reanudado.proyectoGrado());
                case ReanudacionProyectoGradoResult.Omitido omitido ->
                        logger.info(ProyectoGradoKey.LOG_REANUDACION_OMITIDA,
                                omitido.proyectoGrado(), omitido.estadoActual(), omitido.plazoVigente());
            }
        });
    }
}
