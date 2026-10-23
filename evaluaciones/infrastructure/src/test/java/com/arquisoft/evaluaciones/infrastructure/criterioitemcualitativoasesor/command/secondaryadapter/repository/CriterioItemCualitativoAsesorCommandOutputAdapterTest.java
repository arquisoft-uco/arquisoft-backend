package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.entity.CriterioItemCualitativoAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CriterioItemCualitativoAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class CriterioItemCualitativoAsesorCommandOutputAdapterTest {

    @Autowired
    private CriterioItemCualitativoAsesorCommandOutputAdapter adapter;

    @Autowired
    private CriterioItemCualitativoAsesorCommandRepository repository;

    @Test
    void debeRetornarSoloIdsSembrados_cuandoConsultaUnConjuntoMixto() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(CriterioItemCualitativoAsesorJpaEntity.builder()
                .id(existente)
                .nombre("Excelente")
                .descripcion("Cumple todo")
                .build());

        // Act
        var resultado = adapter.consultarIdsExistentes(Set.of(existente, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).containsExactly(existente);
    }
}
