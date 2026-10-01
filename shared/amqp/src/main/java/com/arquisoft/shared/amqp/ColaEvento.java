package com.arquisoft.shared.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;

// Consumir un evento siempre necesita las mismas cuatro declaraciones: la cola, su cola de
// descarte, el binding de esa cola de descarte contra el DLX y el binding de la cola contra el
// exchange de eventos. Escritas a mano son unas cuarenta lineas por evento, todas iguales salvo el
// topic, y basta olvidar el argumento x-dead-letter-exchange en una para que sus mensajes muertos
// se descarten en silencio.
//
// Declarables permite devolverlas como un solo @Bean: RabbitAdmin declara todo lo que hay dentro,
// asi que el consumidor de un evento nuevo cuesta un metodo de tres lineas en vez de cuatro beans.
public final class ColaEvento {

    private ColaEvento() {
    }

    public static String nombre(String prefijo, String topic) {
        return prefijo + topic;
    }

    public static Declarables declarar(
            String cola,
            String routingKey,
            TopicExchange exchangeEventos,
            DirectExchange deadLetterExchange) {

        Queue colaEvento = QueueBuilder
                .durable(cola)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_EXCHANGE, RabbitMQConfig.DLX_NAME)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_ROUTING_KEY, ColaDeadLetter.nombre(cola))
                .build();

        Queue colaDescarte = ColaDeadLetter.declarar(cola);

        Binding bindingEvento = BindingBuilder
                .bind(colaEvento)
                .to(exchangeEventos)
                .with(routingKey);

        return new Declarables(
                colaEvento,
                colaDescarte,
                bindingEvento,
                ColaDeadLetter.enlazar(colaDescarte, deadLetterExchange));
    }
}
