package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaRepositoryTest {

    @Autowired
    private EstadoFichaRepository estadoFichaRepository;

    @Test
    void debeBuscarPorNombre_cuandoEstadoExiste() {
        // Arrange
        var estadoFicha = new EstadoFichaEntity();
        estadoFicha.setId("EN_CONSTRUCCION");
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial");
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
