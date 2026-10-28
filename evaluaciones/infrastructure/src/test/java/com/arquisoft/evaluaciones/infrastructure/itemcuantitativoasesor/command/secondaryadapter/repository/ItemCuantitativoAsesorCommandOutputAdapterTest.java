package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ItemCuantitativoAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCuantitativoAsesorCommandOutputAdapterTest {

    @Autowired
    private ItemCuantitativoAsesorCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarValorPorIdSoloDeLosSembrados_cuandoConsultaValoresMaximos() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var item1 = sembrar("Item uno", categoria, 40);
        var item2 = sembrar("Item dos", categoria, 60);
        sembrar("Item no pedido", categoria, 99);
        var inexistente = UtilUUID.generarNuevoUUID();
        entityManager.flush();

        // Act
        var resultado = adapter.consultarValoresMaximosPorIds(Set.of(item1, item2, inexistente));

        // Assert
        assertThat(resultado).containsOnly(
                java.util.Map.entry(item1, 40),
                java.util.Map.entry(item2, 60));
    }

    @Test
    void debeRetornarMapaVacio_cuandoNingunIdExiste() {
        // Act
        var resultado = adapter.consultarValoresMaximosPorIds(Set.of(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }

    private UUID sembrar(String nombre, UUID categoria, int valor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCuantitativoAsesorJpaEntity.builder()
                .id(id).nombre(nombre).descripcion("descripcion").categoria(categoria).valor(valor).build());
        return id;
    }
}
