package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilCommandOutputAdapterTest {

    @Autowired
    private EstadoFichaPerfilJpaRepository estadoFichaPerfilJpaRepository;

    @Autowired
    private EstadoFichaJpaRepository estadoFichaJpaRepository;

    private EstadoFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilCommandOutputAdapter(
                estadoFichaPerfilJpaRepository, estadoFichaJpaRepository);

        var estadoFicha = new EstadoFichaJpaEntity();
        estadoFicha.setId("EN_CONSTRUCCION");
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial");
        estadoFichaJpaRepository.save(estadoFicha);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        EstadoFichaPerfilAggregate aggregate = EstadoFichaPerfilAggregate.crear(fichaPerfilId);

        // Act
        adapter.guardar(aggregate);

        // Assert
        Optional<EstadoFichaPerfilJpaEntity> resultado = estadoFichaPerfilJpaRepository.findById(aggregate.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(resultado.get().getEstadoFicha().getId()).isEqualTo("EN_CONSTRUCCION");
        assertThat(resultado.get().getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeReconstruirConReconstruir_cuandoMapperConvierte() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        EstadoFichaJpaEntity estadoFichaEntity = estadoFichaJpaRepository.findByNombre("En Construccion")
                .orElseThrow();
        Instant fechaActualizacion = Instant.now();

        var entity = new EstadoFichaPerfilJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFichaEntity);
        entity.setFechaActualizacion(fechaActualizacion);
        estadoFichaPerfilJpaRepository.save(entity);

        // Act
        EstadoFichaPerfilAggregate resultado = EstadoFichaPerfilMapper.toDomain(entity);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());
        assertThat(resultado.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(resultado.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
        assertThat(resultado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
    }
}
