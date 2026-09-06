package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
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
class RevisionItemCommandRepositoryTest {

    @Autowired
    private RevisionItemCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    private void sembrarEstadoRevision(String id) {
        entityManager.createNativeQuery(
                "INSERT INTO estado_revision (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, "Estado de prueba " + id)
                .executeUpdate();
    }

    @Test
    void debeContarRevisiones_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();

        sembrarEstadoRevision("NUEVA");
        sembrarEstadoRevision("VISUALIZADA");

        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)"
        )
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemId)
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now())
                .executeUpdate();

        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)"
        )
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemId)
                .setParameter(3, "VISUALIZADA")
                .setParameter(4, Instant.now().plusSeconds(60))
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        // Act
        long count = repository.countByItemId(itemId);

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    void debeRetornarCero_cuandoItemSinRevisiones() {
        // Arrange
        UUID itemIdSinRevisiones = UUID.randomUUID();

        // Act
        long count = repository.countByItemId(itemIdSinRevisiones);

        // Assert
        assertThat(count).isZero();
    }

    @Test
    void debeActualizarElEstado_cuandoLaRevisionExiste() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID revisionId = UUID.randomUUID();

        sembrarEstadoRevision("NUEVA");
        sembrarEstadoRevision("VISUALIZADA");

        entityManager.createNativeQuery(
                        "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)")
                .setParameter(1, revisionId)
                .setParameter(2, itemId)
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now())
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        // Act
        int filasActualizadas = repository.actualizarEstadoRevision(
                itemId, EstadoRevisionJpaEntity.builder().id("VISUALIZADA").build());
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        var estadoPersistido = entityManager
                .createNativeQuery("SELECT estado_revision_id FROM revision_item WHERE id = ?")
                .setParameter(1, revisionId)
                .getSingleResult();
        assertThat(estadoPersistido).isEqualTo("VISUALIZADA");
    }

    @Test
    void noDebeAfectarOtrasFilas_cuandoActualizaElEstadoDeUnItem() {
        // Arrange
        UUID itemModificado = UUID.randomUUID();
        UUID itemNoTocado = UUID.randomUUID();
        UUID revisionNoTocada = UUID.randomUUID();

        sembrarEstadoRevision("NUEVA");
        sembrarEstadoRevision("VISUALIZADA");

        entityManager.createNativeQuery(
                        "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemModificado)
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now())
                .executeUpdate();

        entityManager.createNativeQuery(
                        "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)")
                .setParameter(1, revisionNoTocada)
                .setParameter(2, itemNoTocado)
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now().plusSeconds(60))
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        // Act
        repository.actualizarEstadoRevision(
                itemModificado, EstadoRevisionJpaEntity.builder().id("VISUALIZADA").build());
        entityManager.flush();
        entityManager.clear();

        // Assert
        var estadoNoTocado = entityManager
                .createNativeQuery("SELECT estado_revision_id FROM revision_item WHERE id = ?")
                .setParameter(1, revisionNoTocada)
                .getSingleResult();
        assertThat(estadoNoTocado).isEqualTo("NUEVA");
    }
}
