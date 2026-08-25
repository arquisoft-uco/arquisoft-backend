package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
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

    @Test
    void debeContarRevisiones_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();

        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)"
        )
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemId)
                .setParameter(3, "ESTADO_PRUEBA_1")
                .setParameter(4, LocalDateTime.now())
                .executeUpdate();

        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)"
        )
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemId)
                .setParameter(3, "ESTADO_PRUEBA_2")
                .setParameter(4, LocalDateTime.now().plusMinutes(1))
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
}
