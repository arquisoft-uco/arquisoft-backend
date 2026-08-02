package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilRepositoryTest {

    @Autowired
    private EstadoFichaPerfilRepository estadoFichaPerfilRepository;

    @Autowired
    private EstadoFichaRepository estadoFichaRepository;

    private EstadoFichaEntity estadoFicha;

    @BeforeEach
    void setUp() {
        estadoFicha = new EstadoFichaEntity();
        estadoFicha.setId("EN_CONSTRUCCION");
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial");
        estadoFichaRepository.save(estadoFicha);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var entity = new EstadoFichaPerfilEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFicha);
        entity.setFechaActualizacion(Instant.now());

        // Act
        EstadoFichaPerfilEntity resultado = estadoFichaPerfilRepository.save(entity);

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
        var entity = new EstadoFichaPerfilEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFicha);
        entity.setFechaActualizacion(Instant.now());
        estadoFichaPerfilRepository.save(entity);

        // Act
        Optional<EstadoFichaPerfilEntity> resultado = estadoFichaPerfilRepository.findById(entity.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
    }
}
