package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.artefactos.versionrepositorioartefacto;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.EliminarVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosArtefactosQueueConfig;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
public class VersionRepositorioArtefactoEliminacionAutorizadaConsumer extends AbstractEventConsumer {

    private static final String HEADER_REINTENTOS = "x-reintentos-eliminacion";
    private static final List<String> COLAS_ESPERA =
            RepositorioArtefactosArtefactosQueueConfig.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERAS;

    private final EliminarVersionRepositorioArtefactoInteractor eliminarVersionRepositorioArtefactoInteractor;
    private final AmqpTemplate amqpTemplate;
    private final AppLogger logger;

    public VersionRepositorioArtefactoEliminacionAutorizadaConsumer(
            EliminarVersionRepositorioArtefactoInteractor eliminarVersionRepositorioArtefactoInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AmqpTemplate amqpTemplate,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.eliminarVersionRepositorioArtefactoInteractor = eliminarVersionRepositorioArtefactoInteractor;
        this.amqpTemplate = amqpTemplate;
        this.logger = logger;
    }

    @RabbitListener(
            queues = RepositorioArtefactosArtefactosQueueConfig.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE)
    public void onVersionRepositorioArtefactoEliminacionAutorizada(Message message, Channel channel)
            throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, VersionRepositorioArtefactoEliminacionAutorizadaPayload.class);

            logger.info(VersionRepositorioArtefactoKey.LOG_ELIMINACION_RECIBIDA,
                    payload.idEvento(), payload.versionRepositorioArtefacto());

            var resultado = eliminarVersionRepositorioArtefactoInteractor.ejecutar(
                    EliminarVersionRepositorioArtefactoCommand.crear(payload.versionRepositorioArtefacto()));

            switch (resultado) {
                case EliminacionVersionRepositorioArtefactoResult.Eliminada eliminada ->
                        logger.info(VersionRepositorioArtefactoKey.LOG_VERSION_ELIMINADA, eliminada.version());
                case EliminacionVersionRepositorioArtefactoResult.Inexistente inexistente ->
                        logger.info(VersionRepositorioArtefactoKey.LOG_VERSION_INEXISTENTE, inexistente.version());
            }
        });
    }

    // Una caida de MinIO o de la base puede durar mas que un reencolado inmediato: se reintenta tras
    // 1 y 5 minutos en colas con TTL, sin nada ejecutandose mientras tanto. Es seguro porque el
    // archivo se borra antes que la fila y borrar un objeto ausente no falla, asi que cada intento
    // retoma donde quedo el anterior. Agotadas las esperas, el mensaje va a la cola de descarte.
    @Override
    protected boolean reintentarTransitorio(Message message, Channel channel, long deliveryTag, Exception ex)
            throws IOException {
        var reintentosPrevios = reintentosPrevios(message);
        if (reintentosPrevios >= COLAS_ESPERA.size()) {
            return false;
        }

        var reintento = reintentosPrevios + 1;
        var colaEspera = COLAS_ESPERA.get(reintentosPrevios);
        message.getMessageProperties().setHeader(HEADER_REINTENTOS, reintento);
        amqpTemplate.send(RabbitMQConfig.EXCHANGE_POR_DEFECTO, colaEspera, message);
        channel.basicAck(deliveryTag, false);

        logger.warn(VersionRepositorioArtefactoKey.LOG_ELIMINACION_REINTENTO_PROGRAMADO,
                reintento, COLAS_ESPERA.size(), colaEspera, ex.getMessage(), ex);
        return true;
    }

    private static int reintentosPrevios(Message message) {
        var valor = message.getMessageProperties().getHeader(HEADER_REINTENTOS);
        return valor instanceof Number numero ? numero.intValue() : 0;
    }
}
