package com.arquisoft.biblioteca.infrastructure.autor.command.primaryadapter.amqp.usuarios.estudiante;

import com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.AgregarAutorInteractor;
import com.arquisoft.biblioteca.application.autor.command.primaryport.model.AgregarAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorKey;
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
public class EstudianteAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarAutorInteractor agregarAutorInteractor;
    private final AppLogger logger;

    public EstudianteAgregadoConsumer(
            AgregarAutorInteractor agregarAutorInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza, logger);
        this.agregarAutorInteractor = agregarAutorInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = BibliotecaUsuariosQueueConfig.ESTUDIANTE_AGREGADO_QUEUE)
    public void onEstudianteAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, EstudianteAgregadoPayload.class);

            logger.info(AutorKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarAutorInteractor.ejecutar(AgregarAutorCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionAutorResult.Agregada agregada ->
                        logger.info(AutorKey.LOG_AGREGADO, agregada.autor());
                case AgregacionAutorResult.Reactivada reactivada ->
                        logger.info(AutorKey.LOG_REACTIVADO, reactivada.autor());
                case AgregacionAutorResult.Duplicada duplicada ->
                        logger.info(AutorKey.LOG_DUPLICADO, duplicada.autor());
                case AgregacionAutorResult.Descartada descartada ->
                        logger.debug(AutorKey.LOG_DESCARTADO,
                                descartada.autor(), descartada.ocurridoEnVigente());
            }
        });
    }
}
