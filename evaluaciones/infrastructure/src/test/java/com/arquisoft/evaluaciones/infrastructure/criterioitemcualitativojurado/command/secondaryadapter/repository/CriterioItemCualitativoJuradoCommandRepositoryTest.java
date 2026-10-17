package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.entity.CriterioItemCualitativoJuradoJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class CriterioItemCualitativoJuradoCommandRepositoryTest {

    @Autowired
    private CriterioItemCualitativoJuradoCommandRepository repository;

    @Test
    void debeRetornarSoloIdsExistentes_cuandoSeConsultaUnConjuntoMixto() {
        // Arrange
        var existente = UUID.randomUUID();
        repository.saveAndFlush(CriterioItemCualitativoJuradoJpaEntity.builder()
                .id(existente)
                .nombre("Excelente")
                .descripcion("Cumple todos los aspectos")
                .build());

        // Act
        var resultado = repository.findIdsByIdIn(Set.of(existente, UUID.randomUUID()));

        // Assert
        assertThat(resultado).containsExactly(existente);
    }
}
