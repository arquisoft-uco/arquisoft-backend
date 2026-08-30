package com.arquisoft.shared.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

// El nombre de la cola de descarte lo escriben dos sitios que no se ven entre si: el argumento
// x-dead-letter-routing-key de la cola de origen y el binding contra el DLX. Si divergen, el
// mensaje muerto se descarta en silencio, que es justo lo que la cola de descarte evita. Por eso
// ambos lados llaman aqui en lugar de componer el nombre por su cuenta.
public final class ColaDeadLetter {

    private ColaDeadLetter() {
    }

    public static String nombre(String colaOrigen) {
        return colaOrigen + RabbitMQConfig.SUFIJO_DEAD_LETTER;
    }

    public static Queue declarar(String colaOrigen) {
        return QueueBuilder
                .durable(nombre(colaOrigen))
                .withArgument(RabbitMQConfig.ARG_MESSAGE_TTL, RabbitMQConfig.TTL_COLA_DEAD_LETTER)
                .build();
    }

    public static Binding enlazar(Queue colaDeadLetter, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(colaDeadLetter)
                .to(deadLetterExchange)
                .with(colaDeadLetter.getName());
    }
}
