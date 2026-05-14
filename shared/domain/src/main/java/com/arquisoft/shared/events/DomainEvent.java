package com.arquisoft.shared.events;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Evento base para todos los eventos de dominio del sistema.
 * Todos los contextos publican eventos que heredan de esta clase.
 */
public abstract class DomainEvent {

    /** Formato obligatorio: {@code {contexto}.{entidad}.{accion}} — tres segmentos en minúsculas separados por puntos. */
    private static final Pattern TOPIC_PATTERN = Pattern.compile("^[a-z][a-z_]*\\.[a-z][a-z_]*\\.[a-z][a-z_]*$");

    private final String eventId;
    private final String aggregateId;
    private final Instant occurredAt;
    private final String eventType;

    protected DomainEvent(String aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredAt = Instant.now();
        this.eventType = this.getClass().getSimpleName();
        // getEventTopic() es seguro aquí: todas las implementaciones retornan un literal
        // de cadena sin depender de estado de la subclase (no hay campos no inicializados).
        validateTopic(this.getEventTopic());
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
     * Topic conceptual al que pertenece este evento.
     * Formato esperado: '{contexto}.{entidad}.{accion}' (ej. 'fichas.ficha.creada').
     *
     * Este valor es agnóstico del broker — la implementación de EventPublisher
     * decide cómo interpretarlo (en RabbitMQ se usa como routing key del
     * exchange arquisoft.events; en otros brokers podría ser un topic de Kafka
     * o un canal genérico).
     */
    public abstract String getEventTopic();
}
