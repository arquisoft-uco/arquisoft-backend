package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EstadoFichaPerfilJpaRepositoryTest {

    @Autowired
    private EstadoFichaPerfilJpaRepository estadoFichaPerfilJpaRepository;

    @Autowired
    private EstadoFichaJpaRepository estadoFichaJpaRepository;

    private EstadoFichaJpaEntity estadoFicha;

    @BeforeEach
    void setUp() {
        estadoFicha = new EstadoFichaJpaEntity();
        estadoFicha.setId("EN_CONSTRUCCION");
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial");
        estadoFichaJpaRepository.save(estadoFicha);
    }

    @Test
    void debeGuardar_cuandoEntidadJpaEsValida() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var entity = new EstadoFichaPerfilJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFicha);
        entity.setFechaActualizacion(Instant.now());

        // Act
        EstadoFichaPerfilJpaEntity resultado = estadoFichaPerfilJpaRepository.save(entity);

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
        var entity = new EstadoFichaPerfilJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFicha);
        entity.setFechaActualizacion(Instant.now());
        estadoFichaPerfilJpaRepository.save(entity);

        // Act
        Optional<EstadoFichaPerfilJpaEntity> resultado = estadoFichaPerfilJpaRepository.findById(entity.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
    }
}
