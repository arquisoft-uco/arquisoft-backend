package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilRepository;
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
    private EstadoFichaPerfilRepository estadoFichaPerfilRepository;

    @Autowired
    private EstadoFichaRepository estadoFichaRepository;

    private EstadoFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilCommandOutputAdapter(
                estadoFichaPerfilRepository, estadoFichaRepository);

        var estadoFicha = EstadoFichaEntity.builder()
                .id("EN_CONSTRUCCION")
                .nombre("En Construccion")
                .descripcion("Estado inicial")
                .build();
        estadoFichaRepository.save(estadoFicha);

        var estadoAprobada = EstadoFichaEntity.builder()
                .id("APROBADA")
                .nombre("Aprobada")
                .descripcion("Estado terminal")
                .build();
        estadoFichaRepository.save(estadoAprobada);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Act
        adapter.registrarEstadoInicial(aggregate);

        // Assert
        Optional<EstadoFichaPerfilEntity> resultado = estadoFichaPerfilRepository.findById(aggregate.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(resultado.get().getEstadoFicha().getId()).isEqualTo("EN_CONSTRUCCION");
        assertThat(resultado.get().getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeReconstruirConReconstruir_cuandoMapperConvierte() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        EstadoFichaEntity estadoFichaEntity = estadoFichaRepository.findByNombre("En Construccion")
                .orElseThrow();
        Instant fechaActualizacion = Instant.now();

        var entity = EstadoFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estadoFicha(estadoFichaEntity)
                .fechaActualizacion(fechaActualizacion)
                .build();
        estadoFichaPerfilRepository.save(entity);

        // Act
        EstadoFichaPerfilDomain resultado = EstadoFichaPerfilMapper.toDomain(entity);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());
        assertThat(resultado.getFichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(resultado.getEstadoFicha()).isEqualTo(EstadoFicha.EN_CONSTRUCCION);
        assertThat(resultado.getFechaActualizacion()).isEqualTo(fechaActualizacion);
    }
}
