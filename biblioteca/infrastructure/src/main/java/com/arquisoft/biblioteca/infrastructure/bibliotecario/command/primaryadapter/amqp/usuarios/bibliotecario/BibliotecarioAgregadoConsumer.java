package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.primaryadapter.amqp.usuarios.bibliotecario;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.AgregarBibliotecarioInteractor;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.AgregarBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.biblioteca.infrastructure.config.BibliotecaUsuariosQueueConfig;
import com.arquisoft.shared.amqp.consumer.AbstractEventConsumer;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
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
public class BibliotecarioAgregadoConsumer extends AbstractEventConsumer {

    private final AgregarBibliotecarioInteractor agregarBibliotecarioInteractor;
    private final AppLogger logger;

    public BibliotecarioAgregadoConsumer(
            AgregarBibliotecarioInteractor agregarBibliotecarioInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger,
            GestorTraza gestorTraza) {
        super(objectMapper, gestorTraza);
        this.agregarBibliotecarioInteractor = agregarBibliotecarioInteractor;
        this.logger = logger;
    }

    @RabbitListener(queues = BibliotecaUsuariosQueueConfig.BIBLIOTECARIO_AGREGADO_QUEUE)
    public void onBibliotecarioAgregado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, BibliotecarioAgregadoPayload.class);

            logger.info(BibliotecarioKey.LOG_AGREGADO_RECIBIDO,
                    payload.idEvento(), payload.usuario(), UtilTexto.enmascararCorreo(payload.email()));

            var resultado = agregarBibliotecarioInteractor.ejecutar(AgregarBibliotecarioCommand.crear(
                    payload.usuario(), payload.identificador(), payload.nombre(), payload.email(),
                    payload.ocurridoEn()));

            switch (resultado) {
                case AgregacionBibliotecarioResult.Agregada agregada ->
                        logger.info(BibliotecarioKey.LOG_AGREGADO, agregada.bibliotecario());
                case AgregacionBibliotecarioResult.Reactivada reactivada ->
                        logger.info(BibliotecarioKey.LOG_REACTIVADO, reactivada.bibliotecario());
                case AgregacionBibliotecarioResult.Duplicada duplicada ->
                        logger.info(BibliotecarioKey.LOG_DUPLICADO, duplicada.bibliotecario());
                case AgregacionBibliotecarioResult.Descartada descartada ->
                        logger.debug(BibliotecarioKey.LOG_DESCARTADO,
                                descartada.bibliotecario(), descartada.ocurridoEnVigente());
            }
        });
    }
}
