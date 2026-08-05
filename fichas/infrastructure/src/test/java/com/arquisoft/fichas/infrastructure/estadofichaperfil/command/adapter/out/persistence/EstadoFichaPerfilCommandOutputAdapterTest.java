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

        var estadoFicha = new EstadoFichaEntity();
        estadoFicha.setId("EN_CONSTRUCCION");
        estadoFicha.setNombre("En Construccion");
        estadoFicha.setDescripcion("Estado inicial");
        estadoFichaRepository.save(estadoFicha);

        var estadoAprobada = new EstadoFichaEntity();
        estadoAprobada.setId("APROBADA");
        estadoAprobada.setNombre("Aprobada");
        estadoAprobada.setDescripcion("Estado terminal");
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

        var entity = new EstadoFichaPerfilEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFichaEntity);
        entity.setFechaActualizacion(fechaActualizacion);
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
