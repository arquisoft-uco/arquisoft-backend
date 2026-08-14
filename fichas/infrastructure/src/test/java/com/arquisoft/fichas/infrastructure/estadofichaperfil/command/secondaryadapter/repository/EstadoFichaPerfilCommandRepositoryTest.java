package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
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

    private EstadoFichaEntity estadoFicha;

    @BeforeEach
    void setUp() {
        estadoFicha = EstadoFichaEntity.builder()
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
        var entity = EstadoFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estadoFicha(estadoFicha)
                .fechaActualizacion(Instant.now())
                .build();

        // Act
        EstadoFichaPerfilEntity resultado = entityManager.persist(entity);

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
        var entity = EstadoFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estadoFicha(estadoFicha)
                .fechaActualizacion(Instant.now())
                .build();
        entityManager.persist(entity);

        // Act
        Optional<EstadoFichaPerfilEntity> resultado = estadoFichaPerfilRepository.findById(entity.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
    }
}
