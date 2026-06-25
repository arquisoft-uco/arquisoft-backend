package com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EstadoFichaQueryOutputAdapterTest {

    @Autowired
    private EstadoFichaJpaRepository estadoFichaJpaRepository;

    private EstadoFichaQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        EstadoFichaMapper mapper = new EstadoFichaMapper();
        adapter = new EstadoFichaQueryOutputAdapter(estadoFichaJpaRepository, mapper);

        var estadoFicha = new EstadoFichaJpaEntity();
        estadoFicha.setId(UUID.randomUUID());
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial de la ficha de perfil");
        estadoFichaJpaRepository.save(estadoFicha);
    }

    @Test
    void debeBuscarPorNombre_cuandoEstadoExiste() {
        // Arrange / Act
        Optional<EstadoFichaReadModel> resultado = adapter.buscarPorNombre("En Construccion");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().nombre()).isEqualTo("En Construccion");
        assertThat(resultado.get().descripcion()).isEqualTo("Estado inicial de la ficha de perfil");
    }

    @Test
    void debeRetornarVacio_cuandoEstadoNoExiste() {
        // Arrange / Act
        Optional<EstadoFichaReadModel> resultado = adapter.buscarPorNombre("Estado Inexistente");

        // Assert
        assertThat(resultado).isEmpty();
    }
}
