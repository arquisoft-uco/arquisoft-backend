package com.arquisoft.shared.events;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class DomainEvent {

    private static final Pattern TOPIC_PATTERN = Pattern.compile("^[a-z][a-z_]*\\.[a-z][a-z_]*\\.[a-z][a-z_]*$");

    private final String eventId;
    private final Instant occurredAt;
    private final String eventType;
    private final String eventTopic;

    protected DomainEvent(String eventTopic, String eventType) {
        validateTopic(eventTopic);
        this.eventId   = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.eventType  = eventType;
        this.eventTopic = eventTopic;
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

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return eventType;
    }

    public final String getEventTopic() {
        return eventTopic;
    }
}
