package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ItemCualitativoAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemCualitativoAsesorCommandOutputAdapterTest {

    @Autowired
    private ItemCualitativoAsesorCommandOutputAdapter adapter;

    @Autowired
    private ItemCualitativoAsesorCommandRepository repository;

    @Test
    void debeRetornarSoloIdsSembrados_cuandoConsultaUnConjuntoMixto() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(ItemCualitativoAsesorJpaEntity.builder()
                .id(existente)
                .nombre("Claridad")
                .descripcion("Evalua la claridad")
                .build());

        // Act
        var resultado = adapter.consultarIdsExistentes(Set.of(existente, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).containsExactly(existente);
    }
}
