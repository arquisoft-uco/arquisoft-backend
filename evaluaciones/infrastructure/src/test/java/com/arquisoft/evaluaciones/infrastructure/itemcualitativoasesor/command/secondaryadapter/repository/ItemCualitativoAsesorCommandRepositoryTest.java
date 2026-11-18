package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCualitativoAsesorCommandRepositoryTest {

    @Autowired
    private ItemCualitativoAsesorCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debeActualizarSoloDescripcion_cuandoItemExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(id)
                .nombre("Claridad conceptual")
                .descripcion("Descripción original")
                .build());
        entityManager.clear();

        // Act
        var filasActualizadas = repository.actualizarDescripcion(id, "Descripción nueva");
        entityManager.clear();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(repository.findById(id)).hasValueSatisfying(persistida -> {
            assertThat(persistida.getNombre()).isEqualTo("Claridad conceptual");
            assertThat(persistida.getDescripcion()).isEqualTo("Descripción nueva");
        });
    }

    @Test
    void debeRetornarCeroFilas_cuandoIdNoExiste() {
        // Arrange
        var idInexistente = UtilUUID.generarNuevoUUID();

        // Act
        var filasActualizadas = repository.actualizarDescripcion(idInexistente, "Descripción nueva");

        // Assert
        assertThat(filasActualizadas).isZero();
    }
}
