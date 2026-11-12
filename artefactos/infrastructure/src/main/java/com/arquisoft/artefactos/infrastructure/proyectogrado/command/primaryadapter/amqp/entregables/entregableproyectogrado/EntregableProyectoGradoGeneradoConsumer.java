package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.CongelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.CongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.artefactos.infrastructure.config.ArtefactosEntregablesQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EntregableProyectoGradoGeneradoConsumer extends AbstractEventConsumer {

    private static final String UN_SOLO_CONSUMIDOR = "1";

    private final CongelarProyectoGradoInteractor congelarProyectoGradoInteractor;
    private final AppLogger logger;

    public EntregableProyectoGradoGeneradoConsumer(
            CongelarProyectoGradoInteractor congelarProyectoGradoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.congelarProyectoGradoInteractor = congelarProyectoGradoInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = ArtefactosEntregablesQueueConfig.ENTREGABLE_PROYECTO_GRADO_GENERADO_QUEUE,
            concurrency = UN_SOLO_CONSUMIDOR)
    public void onEntregableProyectoGradoGenerado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EntregableProyectoGradoGeneradoPayload.class);
            logger.info(ProyectoGradoKey.LOG_ENTREGABLE_GENERADO_RECIBIDO,
                    payload.idEvento(), payload.entregableProyectoGrado(), payload.proyectoGrado(),
                    payload.version());

            var congelacion = congelarProyectoGradoInteractor.ejecutar(
                    CongelarProyectoGradoCommand.crear(payload.proyectoGrado(), payload.ocurridoEn()));

            switch (congelacion) {
                case CongelacionProyectoGradoResult.Congelado congelado ->
                        logger.info(ProyectoGradoKey.LOG_CONGELADO, congelado.proyectoGrado());
                case CongelacionProyectoGradoResult.YaCongelado yaCongelado ->
                        logger.info(ProyectoGradoKey.LOG_YA_CONGELADO, yaCongelado.proyectoGrado());
            }
        });
    }
}
