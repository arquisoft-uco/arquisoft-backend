package com.arquisoft.shared.events;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Evento base para todos los eventos de dominio del sistema.
 * Todos los contextos publican eventos que heredan de esta clase.
 *
 * <p>Las subclases deben pasar el topic canónico al constructor:
 * <pre>{@code
 *   public MiEvento(UUID aggregateId) {
 *       super(aggregateId.toString(), MiEvento.EVENT_TOPIC, MiEvento.EVENT_TYPE);
 *   }
 * }</pre>
 *
 * <p>El topic se valida en construcción (fail-fast). Formato obligatorio:
 * {@code {contexto}.{entidad}.{accion}} — tres segmentos en minúsculas separados por puntos
 * (ej. {@code seguridad.usuario.creado}).
 */
public abstract class DomainEvent {

    /** Formato obligatorio: {@code {contexto}.{entidad}.{accion}} — tres segmentos en minúsculas separados por puntos. */
    private static final Pattern TOPIC_PATTERN = Pattern.compile("^[a-z][a-z_]*\\.[a-z][a-z_]*\\.[a-z][a-z_]*$");

    private final String eventId;
    private final String aggregateId;
    private final Instant occurredAt;
    private final String eventType;
    private final String eventTopic;

    /**
     * @param aggregateId identificador del agregado que origina el evento
     * @param eventTopic  topic canónico del evento en formato {@code contexto.entidad.accion};
     *                    se recomienda usar la constante {@code EVENT_TOPIC} definida en la subclase
     * @param eventType   identificador de tipo estable del evento, desvinculado del nombre
     *                    de la clase Java para resistir renombres sin alterar logs ni contratos;
     *                    se recomienda usar la constante {@code EVENT_TYPE} definida en la subclase
     */
    protected DomainEvent(String aggregateId, String eventTopic, String eventType) {
        validateTopic(eventTopic);
        this.eventId    = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredAt  = Instant.now();
        this.eventType   = eventType;
        this.eventTopic  = eventTopic;
    }

    private static void validateTopic(String topic) {
        if (topic == null || !TOPIC_PATTERN.matcher(topic).matches()) {
            throw new IllegalArgumentException(
                "Event topic '" + topic + "' no cumple el formato requerido '{contexto}.{entidad}.{accion}' "
                + "(ej. 'seguridad.usuario.creado')");
        }
    }

    public String getEventId() {
        return eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return eventType;
    }

    /**
     * Topic canónico de este evento, agnóstico del broker.
     * En RabbitMQ se usa como routing key del exchange {@code arquisoft.events};
     * en otros brokers podría ser un topic de Kafka o un canal genérico.
     */
    public final String getEventTopic() {
        return eventTopic;
    }
}
