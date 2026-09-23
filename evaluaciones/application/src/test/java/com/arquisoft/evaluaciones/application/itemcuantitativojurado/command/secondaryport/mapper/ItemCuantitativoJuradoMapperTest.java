package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCuantitativoJuradoMapperTest {

    @Test
    void debeMapearDomainAEntityYRegresar() {
        // Arrange
        ItemCuantitativoJuradoDomain domain = ItemCuantitativoJuradoDomain.reconstruir(
                UUID.randomUUID(), "Calidad", "Descripción", UUID.randomUUID(), 100);

        // Act
        var entity = ItemCuantitativoJuradoMapper.toEntity(domain);
        var reconstruido = ItemCuantitativoJuradoMapper.toDomain(entity);

        // Assert
        assertThat(reconstruido.getId()).isEqualTo(domain.getId());
        assertThat(reconstruido.getNombre()).isEqualTo(domain.getNombre());
        assertThat(reconstruido.getDescripcion()).isEqualTo(domain.getDescripcion());
        assertThat(reconstruido.getCategoria()).isEqualTo(domain.getCategoria());
        assertThat(reconstruido.getValor()).isEqualTo(domain.getValor());
    }
}
