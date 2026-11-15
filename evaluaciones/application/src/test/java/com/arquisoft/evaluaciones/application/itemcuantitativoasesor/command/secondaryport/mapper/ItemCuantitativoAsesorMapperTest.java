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

    @Test
    void debeMapearEntityADomain_cuandoEntityEsValida() {
        // Arrange
        var entity = new com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity
                .ItemCuantitativoAsesorEntity(
                UtilUUID.generarNuevoUUID(), "Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);

        // Act
        var domain = ItemCuantitativoAsesorMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getNombre()).isEqualTo("Avance");
        assertThat(domain.getValor()).isEqualTo(80);
        assertThat(domain.esVacio()).isFalse();
    }
}
