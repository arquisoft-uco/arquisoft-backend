package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RegistrarItemCualitativoAsesorCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarItemCualitativoAsesorMapperTest {

    @Test
    void debeMapearADomain_cuandoRecibeCommand() {
        // Arrange
        var command = new RegistrarItemCualitativoAsesorCommand("Claridad", "Descripción");

        // Act
        var domain = RegistrarItemCualitativoAsesorMapper.toDomain(command);

        // Assert
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getNombre()).isEqualTo("Claridad");
        assertThat(domain.getDescripcion()).isEqualTo("Descripción");
    }
}
