package com.arquisoft.config.outbox;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.EventPublication.Status;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.modulith.events.core.PublicationTargetIdentifier;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.modulith.events.support.CompletionMode;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContextAwareEventPublicationRepository implements EventPublicationRepository, SmartInitializingSingleton {

    private static final String SQL_COLS =
            "id, listener_id, event_type, serialized_event, publication_date, "
            + "completion_date, status, completion_attempts, last_resubmission_date";

    private final Map<String, DataSource> allDataSources;
    private final Map<DataSource, EntityManagerFactory> dsToEmf;
    private final EventSerializer serializer;
    private final CompletionMode completionMode;
    private final ClassLoader classLoader;
    private final Map<DataSource, String> dataSourceNames;
    private volatile List<OutboxEntry> entries = List.of();

    public ContextAwareEventPublicationRepository(
            Map<String, DataSource> allDataSources,
            Map<String, EntityManagerFactory> allEntityManagerFactories,
            EventSerializer serializer,
            Environment environment) {

        this.allDataSources = allDataSources;
        this.serializer = serializer;
        this.completionMode = CompletionMode.from(environment);
        this.classLoader = ClassUtils.getDefaultClassLoader();
        this.dataSourceNames = allDataSources.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a));
        this.dsToEmf = allEntityManagerFactories.values().stream()
                .filter(emf -> emf instanceof EntityManagerFactoryInfo)
                .collect(Collectors.toMap(
                        emf -> ((EntityManagerFactoryInfo) emf).getDataSource(),
                        emf -> emf,
                        (a, b) -> a));
    }

    /**
     * Se ejecuta después de que TODOS los beans están inicializados, garantizando
     * que Flyway ya aplicó las migraciones antes de verificar qué DataSources
     * tienen tabla event_publication.
     */
    @Override
    public void afterSingletonsInstantiated() {
        this.entries = allDataSources.entrySet().stream()
                .filter(e -> hasEventPublicationTable(e.getValue()))
                .map(e -> new OutboxEntry(dsToEmf.get(e.getValue()), e.getValue(),
                        new JdbcTemplate(e.getValue())))
                .peek(e -> log.info("Outbox habilitado: DataSource '{}'",
                        dataSourceNames.getOrDefault(e.dataSource(), "desconocido")))
                .toList();

        if (entries.isEmpty()) {
            log.warn("Outbox: ningún DataSource tiene tabla event_publication. "
                    + "Agrega la migración V{N}__crear_event_publication.sql en el contexto correspondiente.");
        }
    }

    private JdbcTemplate resolveActiveTemplate() {
        Map<Object, Object> resources = TransactionSynchronizationManager.getResourceMap();
        for (OutboxEntry entry : entries) {
            if (entry.emf() != null && resources.containsKey(entry.emf())) {
                return entry.template();
            }
            if (resources.containsKey(entry.dataSource())) {
                return entry.template();
            }
        }
        throw new IllegalStateException(
                "ContextAwareEventPublicationRepository: ningún contexto tiene transacción activa. "
                        + "Verifica @Transactional(transactionManager=...) en el use case.");
    }

    private static boolean hasEventPublicationTable(DataSource dataSource) {
        try {
            new JdbcTemplate(dataSource).queryForObject(
                    "SELECT COUNT(*) FROM event_publication", Integer.class);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void fanOutUpdate(String sql, Object... args) {
        for (OutboxEntry entry : entries) {
            if (entry.template().update(sql, args) > 0) {
                return;
            }
        }
    }

    private List<TargetEventPublication> fanOutQuery(String sql, Object... args) {
        return entries.stream()
                .flatMap(e -> e.template().query(sql, this::mapRow, args).stream())
                .filter(Objects::nonNull)
                .toList();
    }

    private TargetEventPublication mapRow(ResultSet rs, int rowNum) throws SQLException {
        return resultSetToPublication(rs);
    }

    @Override
    public TargetEventPublication create(TargetEventPublication publication) {
        resolveActiveTemplate().update(
                "INSERT INTO event_publication "
                        + "(id, listener_id, event_type, serialized_event, publication_date, "
                        + "status, completion_attempts, last_resubmission_date) "
                        + "VALUES (?, ?, ?, ?, ?, 'OPEN', 0, NULL)",
                publication.getIdentifier(),
                publication.getTargetIdentifier().getValue(),
                publication.getEvent().getClass().getName(),
                serializer.serialize(publication.getEvent()).toString(),
                Timestamp.from(publication.getPublicationDate()));
        return publication;
    }

    @Override
    public void markCompleted(UUID identifier, Instant completionDate) {
        if (completionMode == CompletionMode.DELETE) {
            fanOutUpdate("DELETE FROM event_publication WHERE id = ?", identifier);
        } else {
            fanOutUpdate(
                    "UPDATE event_publication SET completion_date = ?, status = 'COMPLETED', "
                            + "completion_attempts = completion_attempts + 1 WHERE id = ?",
                    Timestamp.from(completionDate), identifier);
        }
    }

    @Override
    public void markCompleted(Object event, PublicationTargetIdentifier identifier, Instant completionDate) {
        String serializedEvent = serializer.serialize(event).toString();
        if (completionMode == CompletionMode.DELETE) {
            fanOutUpdate(
                    "DELETE FROM event_publication "
                            + "WHERE serialized_event = ? AND listener_id = ? AND completion_date IS NULL",
                    serializedEvent, identifier.getValue());
        } else {
            fanOutUpdate(
                    "UPDATE event_publication SET completion_date = ?, status = 'COMPLETED', "
                            + "completion_attempts = completion_attempts + 1 "
                            + "WHERE serialized_event = ? AND listener_id = ? AND completion_date IS NULL",
                    Timestamp.from(completionDate), serializedEvent, identifier.getValue());
        }
    }

    @Override
    public void markProcessing(UUID identifier) {
        fanOutUpdate("UPDATE event_publication SET status = 'PROCESSING' WHERE id = ?", identifier);
    }

    @Override
    public void markFailed(UUID identifier) {
        fanOutUpdate("UPDATE event_publication SET status = 'FAILED' WHERE id = ?", identifier);
    }

    @Override
    public boolean markResubmitted(UUID identifier, Instant resubmissionDate) {
        for (OutboxEntry entry : entries) {
            int rows = entry.template().update(
                    "UPDATE event_publication SET status = 'RESUBMITTED', last_resubmission_date = ?, "
                            + "completion_attempts = completion_attempts + 1 "
                            + "WHERE id = ? AND status NOT IN ('COMPLETED', 'RESUBMITTED')",
                    Timestamp.from(resubmissionDate), identifier);
            if (rows > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<TargetEventPublication> findIncompletePublications() {
        return fanOutQuery(
                "SELECT " + SQL_COLS + " FROM event_publication WHERE completion_date IS NULL");
    }

    @Override
    public List<TargetEventPublication> findIncompletePublicationsPublishedBefore(Instant instant) {
        return fanOutQuery(
                "SELECT " + SQL_COLS + " FROM event_publication "
                        + "WHERE completion_date IS NULL AND publication_date < ?",
                Timestamp.from(instant));
    }

    @Override
    public Optional<TargetEventPublication> findIncompletePublicationsByEventAndTargetIdentifier(
            Object event, PublicationTargetIdentifier targetIdentifier) {
        return fanOutQuery(
                "SELECT " + SQL_COLS + " FROM event_publication "
                        + "WHERE serialized_event = ? AND listener_id = ? AND completion_date IS NULL",
                serializer.serialize(event).toString(), targetIdentifier.getValue())
                .stream().findFirst();
    }

    @Override
    public List<TargetEventPublication> findCompletedPublications() {
        return fanOutQuery(
                "SELECT " + SQL_COLS + " FROM event_publication WHERE completion_date IS NOT NULL");
    }

    @Override
    public List<TargetEventPublication> findFailedPublications(FailedCriteria criteria) {
        String baseSql = "SELECT " + SQL_COLS + " FROM event_publication WHERE status = 'FAILED'";
        Instant dateRef = criteria.getPublicationDateReference();
        long maxItems = criteria.getMaxItemsToRead();

        if (dateRef != null && maxItems > 0) {
            return fanOutQuery(baseSql + " AND publication_date < ? LIMIT ?",
                    Timestamp.from(dateRef), maxItems);
        } else if (dateRef != null) {
            return fanOutQuery(baseSql + " AND publication_date < ?", Timestamp.from(dateRef));
        } else if (maxItems > 0) {
            return fanOutQuery(baseSql + " LIMIT ?", maxItems);
        }
        return fanOutQuery(baseSql);
    }

    @Override
    public List<TargetEventPublication> findByStatus(Status status) {
        return fanOutQuery(
                "SELECT " + SQL_COLS + " FROM event_publication WHERE status = ?", status.name());
    }

    @Override
    public int countByStatus(Status status) {
        return entries.stream()
                .mapToInt(e -> {
                    Integer count = e.template().queryForObject(
                            "SELECT COUNT(*) FROM event_publication WHERE status = ?",
                            Integer.class, status.name());
                    return count != null ? count : 0;
                })
                .sum();
    }

    @Override
    public void deletePublications(List<UUID> identifiers) {
        if (identifiers.isEmpty()) {
            return;
        }
        String placeholders = identifiers.stream().map(id -> "?").collect(Collectors.joining(","));
        for (OutboxEntry entry : entries) {
            entry.template().update(
                    "DELETE FROM event_publication WHERE id IN (" + placeholders + ")",
                    identifiers.toArray());
        }
    }

    @Override
    public void deleteCompletedPublications() {
        entries.forEach(e -> e.template()
                .update("DELETE FROM event_publication WHERE completion_date IS NOT NULL"));
    }

    @Override
    public void deleteCompletedPublicationsBefore(Instant instant) {
        entries.forEach(e -> e.template().update(
                "DELETE FROM event_publication WHERE completion_date < ?",
                Timestamp.from(instant)));
    }

    private TargetEventPublication resultSetToPublication(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String eventType = rs.getString("event_type");
        Class<?> eventClass;
        try {
            eventClass = ClassUtils.forName(eventType, classLoader);
        } catch (ClassNotFoundException e) {
            log.warn("Evento '{}' con tipo desconocido '{}' encontrado en event_publication", id, eventType);
            return null;
        }
        Timestamp completionTs = rs.getTimestamp("completion_date");
        String serializedEvent = rs.getString("serialized_event");
        return new JdbcEventPublication(
                id,
                rs.getTimestamp("publication_date").toInstant(),
                rs.getString("listener_id"),
                () -> serializer.deserialize(serializedEvent, eventClass),
                completionTs == null ? null : completionTs.toInstant());
    }

    private record OutboxEntry(EntityManagerFactory emf, DataSource dataSource, JdbcTemplate template) {}

    private static class JdbcEventPublication implements TargetEventPublication {

        private final UUID id;
        private final Instant publicationDate;
        private final String listenerId;
        private final Supplier<Object> eventSupplier;
        private Instant completionDate;
        private Object event;

        JdbcEventPublication(UUID id, Instant publicationDate, String listenerId,
                             Supplier<Object> eventSupplier, Instant completionDate) {
            this.id = id;
            this.publicationDate = publicationDate;
            this.listenerId = listenerId;
            this.eventSupplier = eventSupplier;
            this.completionDate = completionDate;
        }

        @Override
        public UUID getIdentifier() {
            return id;
        }

        @Override
        public Object getEvent() {
            if (event == null) {
                event = eventSupplier.get();
            }
            return event;
        }

        @Override
        public PublicationTargetIdentifier getTargetIdentifier() {
            return PublicationTargetIdentifier.of(listenerId);
        }

        @Override
        public Instant getPublicationDate() {
            return publicationDate;
        }

        @Override
        public Optional<Instant> getCompletionDate() {
            return Optional.ofNullable(completionDate);
        }

        @Override
        public boolean isPublicationCompleted() {
            return completionDate != null;
        }

        @Override
        public void markCompleted(Instant instant) {
            this.completionDate = instant;
        }

        @Override
        public Status getStatus() {
            return completionDate != null ? Status.COMPLETED : Status.PROCESSING;
        }

        @Override
        public Instant getLastResubmissionDate() {
            return null;
        }

        @Override
        public int getCompletionAttempts() {
            return 1;
        }
    }
}
