package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ObservacionItemCommandRepositoryTest {

    @Autowired
    private ObservacionItemCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    private void sembrarEstadoObservacionRevision(String id) {
        entityManager.createNativeQuery(
                "INSERT INTO estado_observacion_revision (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, "Estado de prueba " + id)
                .executeUpdate();
    }

    private void sembrarEstadoRevision(String id) {
        entityManager.createNativeQuery(
                "INSERT INTO estado_revision (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, "Estado de prueba " + id)
                .executeUpdate();
    }

    private UUID sembrarRevisionItem() {
        var revisionItemId = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)")
                .setParameter(1, revisionItemId)
                .setParameter(2, UUID.randomUUID())
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now())
                .executeUpdate();
        return revisionItemId;
    }

    private void sembrarObservacionItem(UUID revisionItemId, String observacion) {
        entityManager.createNativeQuery(
                "INSERT INTO observacion_item (id, revision_item_id, observacion, "
                        + "estado_observacion_revision_id) VALUES (?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, revisionItemId)
                .setParameter(3, observacion)
                .setParameter(4, "PENDIENTE")
                .executeUpdate();
    }

    @Test
    void debeContarUno_cuandoElTextoCoincideExactamenteEnLaMismaRevision() {
        // Arrange
        sembrarEstadoObservacionRevision("PENDIENTE");
        sembrarEstadoRevision("NUEVA");
        var revisionItemId = sembrarRevisionItem();
        sembrarObservacionItem(revisionItemId, "Observación válida");
        entityManager.flush();
        entityManager.clear();

        // Act
        var count = repository.countByRevisionItemIdAndObservacion(revisionItemId, "Observación válida");

        // Assert
        assertThat(count).isEqualTo(1);
    }

    @Test
    void debeRetornarCero_cuandoElTextoEsDistinto() {
        // Arrange
        sembrarEstadoObservacionRevision("PENDIENTE");
        sembrarEstadoRevision("NUEVA");
        var revisionItemId = sembrarRevisionItem();
        sembrarObservacionItem(revisionItemId, "Observación válida");
        entityManager.flush();
        entityManager.clear();

        // Act
        var count = repository.countByRevisionItemIdAndObservacion(revisionItemId, "Otro texto");

        // Assert
        assertThat(count).isZero();
    }

    @Test
    void debeRetornarCero_cuandoElTextoCoincideEnOtraRevision() {
        // Arrange
        sembrarEstadoObservacionRevision("PENDIENTE");
        sembrarEstadoRevision("NUEVA");
        var revisionItemId = sembrarRevisionItem();
        var otraRevisionItemId = sembrarRevisionItem();
        sembrarObservacionItem(revisionItemId, "Observación válida");
        entityManager.flush();
        entityManager.clear();

        // Act
        var count = repository.countByRevisionItemIdAndObservacion(otraRevisionItemId, "Observación válida");

        // Assert
        assertThat(count).isZero();
    }
}
