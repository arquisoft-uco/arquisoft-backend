package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCualitativoJuradoCommandRepositoryTest {

    @Autowired
    private ItemCualitativoJuradoCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debePersistirLosCampos_cuandoEntidadEsValida() {
        // Arrange
        UUID id = UUID.randomUUID();
        var entity = ItemCualitativoJuradoJpaEntity.builder()
                .id(id)
                .nombre("Claridad")
                .descripcion("Evalúa la claridad conceptual")
                .build();

        // Act
        repository.saveAndFlush(entity);
        entityManager.clear();

        // Assert
        assertThat(repository.findById(id)).hasValueSatisfying(persistida -> {
            assertThat(persistida.getNombre()).isEqualTo("Claridad");
            assertThat(persistida.getDescripcion()).isEqualTo("Evalúa la claridad conceptual");
        });
    }

    @Test
    void debeEncontrarNombre_cuandoCambiaMayusculasYMinusculas() {
        // Arrange
        repository.saveAndFlush(ItemCualitativoJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .nombre("Claridad")
                .descripcion("Descripción")
                .build());
        entityManager.clear();

        // Act
        boolean existe = repository.existsByNombreIgnoreCase("cLaRiDaD");

        // Assert
        assertThat(existe).isTrue();
    }
}
