package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RegistrarItemCuantitativoAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarItemCuantitativoAsesorMapperTest {

    @Test
    void debeMapearCommandADomain_cuandoCommandEsValido() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var command = new RegistrarItemCuantitativoAsesorCommand("Avance", "Porcentaje", categoria, 80);

        // Act
        var domain = RegistrarItemCuantitativoAsesorMapper.toDomain(command);

        // Assert
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getNombre()).isEqualTo("Avance");
        assertThat(domain.getDescripcion()).isEqualTo("Porcentaje");
        assertThat(domain.getCategoria()).isEqualTo(categoria);
        assertThat(domain.getValor()).isEqualTo(80);
    }
}
