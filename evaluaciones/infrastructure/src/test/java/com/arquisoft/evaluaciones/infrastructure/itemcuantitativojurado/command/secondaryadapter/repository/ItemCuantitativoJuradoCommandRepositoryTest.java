package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCuantitativoJuradoCommandRepositoryTest {

    @Autowired
    private ItemCuantitativoJuradoCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debeActualizarSoloDescripcion_cuandoItemExiste() {
        // Arrange
        var id = UUID.randomUUID();
        repository.saveAndFlush(ItemCuantitativoJuradoJpaEntity.builder()
                .id(id)
                .nombre("Calidad técnica")
                .descripcion("Descripción original")
                .categoriaId(UUID.randomUUID())
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
        var idInexistente = UUID.randomUUID();

        // Act
        var filasActualizadas = repository.actualizarDescripcion(idInexistente, "Descripción nueva");

        // Assert
        assertThat(filasActualizadas).isZero();
    }

    @Test
    void debeDevolverValorMaximoSoloDeLosIdsPedidos_cuandoBuscaValoresMaximosPorIds() {
        // Arrange
        var pedido = UtilUUID.generarNuevoUUID();
        var noPedido = UtilUUID.generarNuevoUUID();
        sembrarItem(pedido, 150);
        sembrarItem(noPedido, 300);

        // Act
        var resultado = repository.findValoresMaximosByIdIn(Set.of(pedido, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado)
                .extracting(proyeccion -> proyeccion.getId(), proyeccion -> proyeccion.getValor())
                .containsExactly(tuple(pedido, 150));
    }

    private void sembrarItem(UUID id, int valor) {
        repository.saveAndFlush(ItemCuantitativoJuradoJpaEntity.builder()
                .id(id)
                .nombre("Item " + id)
                .descripcion("Descripción")
                .categoriaId(UtilUUID.generarNuevoUUID())
                .valor(valor)
                .build());
        entityManager.clear();
    }
}
