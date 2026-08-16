package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilCommandRepositoryTest {

    @Autowired
    private EstadoFichaPerfilCommandRepository estadoFichaPerfilRepository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoFichaJpaEntity estadoFicha;

    @BeforeEach
    void setUp() {
        estadoFicha = EstadoFichaJpaEntity.builder()
                .id("EN_CONSTRUCCION")
                .nombre("En Construccion")
                .descripcion("Estado inicial")
                .build();
        entityManager.persist(estadoFicha);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var entity = EstadoFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estadoFicha(estadoFicha)
                .fechaActualizacion(Instant.now())
                .build();

        // Act
        EstadoFichaPerfilJpaEntity resultado = entityManager.persist(entity);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());
        assertThat(resultado.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(resultado.getEstadoFicha().getId()).isEqualTo("EN_CONSTRUCCION");
    }

    @Test
    void debeBuscarPorId_cuandoIdExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var entity = EstadoFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estadoFicha(estadoFicha)
                .fechaActualizacion(Instant.now())
                .build();
        entityManager.persist(entity);

        // Act
        Optional<EstadoFichaPerfilJpaEntity> resultado = estadoFichaPerfilRepository.findById(entity.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
    }
}
