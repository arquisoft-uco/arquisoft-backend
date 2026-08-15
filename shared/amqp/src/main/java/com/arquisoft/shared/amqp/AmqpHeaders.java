package com.arquisoft.shared.amqp;

/**
 * Cabeceras de correlación que viajan en los mensajes AMQP.
 *
 * <p>No van al catálogo de mensajes: no son texto que alguien lea, son el nombre exacto de la
 * cabecera que el productor escribe y el consumidor lee. Existen como constantes por la misma
 * razón que {@code MdcKeys} o los {@code *Codes} — son contrato, y estaban repetidas como literal
 * en {@code RabbitMQConfig} y {@code AbstractEventConsumer}, donde una errata en uno de los dos
 * lados habría roto la correlación en silencio.
 */
public final class AmqpHeaders {

    private AmqpHeaders() {}

    public static final String X_TRACE_ID = "X-Trace-Id";
    public static final String X_USER_ID = "X-User-Id";
}
