package com.arquisoft.biblioteca.infrastructure.autor.command.primaryadapter.amqp.usuarios.estudiante;

import com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.RemoverAutorInteractor;
import com.arquisoft.biblioteca.application.autor.command.primaryport.model.RemoverAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudianteRemovidoConsumer extends AbstractEventConsumer {

    private final RemoverAutorInteractor removerAutorInteractor;
    private final AppLogger logger;

    public EstudianteRemovidoConsumer(
            RemoverAutorInteractor removerAutorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.removerAutorInteractor = removerAutorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = BibliotecaUsuariosQueueConfig.ESTUDIANTE_REMOVIDO_QUEUE)
    public void onEstudianteRemovido(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteRemovidoPayload.class);

            logger.info(AutorKey.LOG_REMOVIDO_RECIBIDO, payload.idEvento(), payload.usuario());

            var resultado = removerAutorInteractor.ejecutar(RemoverAutorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case RemocionAutorResult.Removida removida ->
                        logger.info(AutorKey.LOG_REMOVIDO, removida.autor());
                case RemocionAutorResult.Lapida lapida ->
                        logger.info(AutorKey.LOG_LAPIDA, lapida.autor());
                case RemocionAutorResult.Descartada descartada ->
                        logger.debug(AutorKey.LOG_REMOCION_DESCARTADA,
                                descartada.autor(), descartada.ocurridoEnVigente());
            }
        });
    }
}
