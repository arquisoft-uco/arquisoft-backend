package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;
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
class ItemCuantitativoAsesorCommandRepositoryTest {

    @Autowired
    private ItemCuantitativoAsesorCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debeActualizarSoloDescripcion_cuandoItemExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCuantitativoAsesorJpaEntity.builder()
                .id(id)
                .nombre("Calidad técnica")
                .descripcion("Descripción original")
                .categoria(UtilUUID.generarNuevoUUID())
                .valor(100)
                .build());
        entityManager.clear();

        // Act
        var filasActualizadas = repository.actualizarDescripcion(id, "Descripción nueva");
        entityManager.clear();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(repository.findById(id)).hasValueSatisfying(persistida -> {
            assertThat(persistida.getNombre()).isEqualTo("Calidad técnica");
            assertThat(persistida.getDescripcion()).isEqualTo("Descripción nueva");
            assertThat(persistida.getValor()).isEqualTo(100);
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
