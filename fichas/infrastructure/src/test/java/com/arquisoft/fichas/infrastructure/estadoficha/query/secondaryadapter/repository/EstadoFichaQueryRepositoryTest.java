package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaQueryRepositoryTest {

    @Autowired
    private EstadoFichaQueryRepository estadoFichaRepository;

    @Test
    void debeBuscarPorNombre_cuandoEstadoExiste() {
        // Arrange
        var estadoFicha = EstadoFichaEntity.builder()
                .id("EN_CONSTRUCCION")
                .nombre("En Construccion")
                .descripcion("Estado inicial")
                .build();
        estadoFichaRepository.save(estadoFicha);

        // Act
        Optional<EstadoFichaEntity> resultado = estadoFichaRepository.findByNombre("En Construccion");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("En Construccion");
        assertThat(resultado.get().getDescripcion()).isEqualTo("Estado inicial");
    }

    @Test
    void debeRetornarVacio_cuandoEstadoNoExiste() {
        // Arrange / Act
        Optional<EstadoFichaEntity> resultado = estadoFichaRepository.findByNombre("Estado Inexistente");

        // Assert
        assertThat(resultado).isEmpty();
    }
}
