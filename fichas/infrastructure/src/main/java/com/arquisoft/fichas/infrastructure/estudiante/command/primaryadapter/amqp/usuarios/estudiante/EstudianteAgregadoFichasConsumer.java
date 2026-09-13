package com.arquisoft.fichas.infrastructure.estudiante.command.primaryadapter.amqp.usuarios.estudiante;

import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.AgregarEstudianteFichasInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.infrastructure.config.FichasUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class EstudianteAgregadoFichasConsumer extends AbstractEventConsumer {

    private final AgregarEstudianteFichasInteractor agregarEstudianteFichasInteractor;
    private final AppLogger logger;

    public EstudianteAgregadoFichasConsumer(
            AgregarEstudianteFichasInteractor agregarEstudianteFichasInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarEstudianteFichasInteractor = agregarEstudianteFichasInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = FichasUsuariosQueueConfig.ESTUDIANTE_AGREGADO_QUEUE)
    public void onEstudianteAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteAgregadoPayload.class);

            logger.info(EstudianteKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarEstudianteFichasInteractor.ejecutar(AgregarEstudianteCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionEstudianteResult.Agregada agregada ->
                        logger.info(EstudianteKey.LOG_AGREGADO, agregada.estudiante());
                case AgregacionEstudianteResult.Duplicada duplicada ->
                        logger.info(EstudianteKey.LOG_DUPLICADO, duplicada.estudiante());
                case AgregacionEstudianteResult.Descartada descartada ->
                        logger.debug(EstudianteKey.LOG_DESCARTADO,
                                descartada.estudiante(), descartada.ocurridoEnVigente());
            }
        });
    }
}
