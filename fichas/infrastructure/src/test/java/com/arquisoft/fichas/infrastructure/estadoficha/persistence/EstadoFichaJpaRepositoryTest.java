package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaJpaRepositoryTest {

    @Autowired
    private EstadoFichaJpaRepository estadoFichaJpaRepository;

    @Test
    void debeBuscarPorNombre_cuandoEstadoExiste() {
        // Arrange
        var estadoFicha = new EstadoFichaJpaEntity();
        estadoFicha.setId("EN_CONSTRUCCION");
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial");
        estadoFichaJpaRepository.save(estadoFicha);

        // Act
        Optional<EstadoFichaJpaEntity> resultado = estadoFichaJpaRepository.findByNombre("En Construccion");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("En Construccion");
        assertThat(resultado.get().getDescripcion()).isEqualTo("Estado inicial");
    }

    @Test
    void debeRetornarVacio_cuandoEstadoNoExiste() {
        // Arrange / Act
        Optional<EstadoFichaJpaEntity> resultado = estadoFichaJpaRepository.findByNombre("Estado Inexistente");

        // Assert
        assertThat(resultado).isEmpty();
    }
}
