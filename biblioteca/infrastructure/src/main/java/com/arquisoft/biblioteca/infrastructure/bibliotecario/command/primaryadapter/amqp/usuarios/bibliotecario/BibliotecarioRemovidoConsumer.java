package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.primaryadapter.amqp.usuarios.bibliotecario;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.RemoverBibliotecarioInteractor;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class BibliotecarioRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverBibliotecarioInteractor removerBibliotecarioInteractor;
    private final AppLogger logger;

    public BibliotecarioRemovidoConsumer(
            RemoverBibliotecarioInteractor removerBibliotecarioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.removerBibliotecarioInteractor = removerBibliotecarioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = BibliotecaUsuariosQueueConfig.BIBLIOTECARIO_REMOVIDO_QUEUE)
    public void onBibliotecarioRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, BibliotecarioRemovidoPayload.class);

            logger.info(BibliotecarioKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerBibliotecarioInteractor.ejecutar(RemoverBibliotecarioCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionBibliotecarioResult.Removida removida ->
                        logger.info(BibliotecarioKey.LOG_REMOVIDO, removida.bibliotecario());
                case RemocionBibliotecarioResult.Lapida lapida ->
                        logger.info(BibliotecarioKey.LOG_LAPIDA, lapida.bibliotecario());
                case RemocionBibliotecarioResult.Descartada descartada ->
                        logger.debug(BibliotecarioKey.LOG_REMOCION_DESCARTADA,
                                descartada.bibliotecario(), descartada.ocurridoEnVigente());
            }
        });
    }
}
