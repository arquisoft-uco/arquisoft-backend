package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCuantitativoAsesorMapperTest {

    @Test
    void debeMapearDomainAEntity_cuandoDomainEsValido() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var domain = ItemCuantitativoAsesorDomain.crear("Avance", "Porcentaje", categoria, 80);

        // Act
        var entity = ItemCuantitativoAsesorMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.nombre()).isEqualTo("Avance");
        assertThat(entity.descripcion()).isEqualTo("Porcentaje");
        assertThat(entity.categoriaId()).isEqualTo(categoria);
        assertThat(entity.valor()).isEqualTo(80);
    }
}
