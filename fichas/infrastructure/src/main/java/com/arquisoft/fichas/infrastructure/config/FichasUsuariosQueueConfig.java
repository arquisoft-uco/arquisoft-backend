package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declaración de colas y bindings del contexto Fichas para eventos publicados
 * por el contexto Seguridad relativos a usuarios.
 *
 * <p>Cada cola es durable y exclusiva de este contexto — ningún otro bounded context
 * comparte estas colas. La misma routing key puede estar vinculada a otras colas
 * en otros contextos sin interferencia.
 *
 * <p>Dead Letter: mensajes rechazados tras agotar reintentos en el consumer son
 * reenviados al exchange {@value RabbitMQConfig#DLX_NAME} con routing key
 * {@code {queue-name}.dead} para inspección manual o reprocesamiento.
 *
 * <p>Nomenclatura de colas: {@code {contexto-consumidor}.{routing-key-del-evento}}.
 */
@Configuration
public class FichasUsuariosQueueConfig {

    /**
     * Nombre de la cola donde Fichas recibe eventos {@code seguridad.usuario.creado}.
     * Debe coincidir con el routing key definido en
     * {@link com.arquisoft.fichas.infrastructure.adapter.in.amqp.UsuarioCreadoConsumer}.
     */
    public static final String USUARIO_CREADO_QUEUE = "fichas.seguridad.usuario.creado";

    @Bean
    public Queue fichasUsuarioCreadoQueue() {
        return QueueBuilder
                .durable(USUARIO_CREADO_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", USUARIO_CREADO_QUEUE + ".dead")
                .build();
    }

    /**
     * Vincula la cola al exchange central usando la routing key del evento.
     * RabbitMQ entregará una copia del evento a esta cola cada vez que
     * Seguridad publique con routing key {@code seguridad.usuario.creado}.
     */
    @Bean
    public Binding fichasUsuarioCreadoBinding(
            Queue fichasUsuarioCreadoQueue,
            TopicExchange arquisoftEventsExchange) {
        return BindingBuilder
                .bind(fichasUsuarioCreadoQueue)
                .to(arquisoftEventsExchange)
                .with("seguridad.usuario.creado");
    }
}
