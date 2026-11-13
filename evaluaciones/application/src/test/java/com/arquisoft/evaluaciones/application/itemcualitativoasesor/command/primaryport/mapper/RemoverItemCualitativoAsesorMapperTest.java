package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RemoverItemCualitativoAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverItemCualitativoAsesorMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = RemoverItemCualitativoAsesorCommand.crear(item);

        // Act
        var remocion = RemoverItemCualitativoAsesorMapper.toDomain(command);

        // Assert
        assertThat(remocion.getItemCualitativoAsesor()).isEqualTo(item);
    }
}
