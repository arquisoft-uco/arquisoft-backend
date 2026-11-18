package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RemoverItemCuantitativoAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverItemCuantitativoAsesorMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = RemoverItemCuantitativoAsesorCommand.crear(item);

        // Act
        var remocion = RemoverItemCuantitativoAsesorMapper.toDomain(command);

        // Assert
        assertThat(remocion.getItemCuantitativoAsesor()).isEqualTo(item);
    }
}
