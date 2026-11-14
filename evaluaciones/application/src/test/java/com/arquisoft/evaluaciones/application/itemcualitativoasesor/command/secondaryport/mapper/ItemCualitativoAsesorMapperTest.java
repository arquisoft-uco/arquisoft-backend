package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCualitativoAsesorMapperTest {

    @Test
    void debeMapearDomainAEntity_cuandoRecibeDomain() {
        // Arrange
        var domain = ItemCualitativoAsesorDomain.crear("Claridad", "Descripción");

        // Act
        var entity = ItemCualitativoAsesorMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.nombre()).isEqualTo("Claridad");
        assertThat(entity.descripcion()).isEqualTo("Descripción");
    }
}
