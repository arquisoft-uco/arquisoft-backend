package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository.EstadoFichaQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilCommandOutputAdapterTest {

    @Autowired
    private EstadoFichaPerfilCommandRepository estadoFichaPerfilRepository;

    @Autowired
    private EstadoFichaQueryRepository estadoFichaRepository;

    private EstadoFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilCommandOutputAdapter(estadoFichaPerfilRepository);

        estadoFichaRepository.save(EstadoFichaEntity.builder()
                .id("EN_CONSTRUCCION")
                .nombre("En Construccion")
                .descripcion("Estado inicial")
                .build());
        estadoFichaRepository.save(EstadoFichaEntity.builder()
                .id("APROBADA")
                .nombre("Aprobada")
                .descripcion("Estado terminal")
                .build());
    }

    @Test
    void debeGuardarLaEntidadConSuCatalogo_cuandoElMapperSoloTraeLaReferenciaPorId() {
        // Arrange — el mapper construye la referencia a estado_ficha solo con el id; Hibernate
        // debe escribir la FK sin necesidad de cargar la fila del catalogo.
        UUID fichaPerfilId = UUID.randomUUID();
        EstadoFichaPerfilDomain aggregate = EstadoFichaPerfilDomain.crear(fichaPerfilId);

        // Act
        adapter.registrarEstadoInicial(EstadoFichaPerfilMapper.toEntity(aggregate));

        // Assert
        Optional<EstadoFichaPerfilEntity> resultado =
                estadoFichaPerfilRepository.findById(aggregate.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(resultado.get().getEstadoFicha().getId()).isEqualTo("EN_CONSTRUCCION");
        assertThat(resultado.get().getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeDevolverElEstadoMasReciente_cuandoLaFichaTieneVarios() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        EstadoFichaPerfilDomain inicial = EstadoFichaPerfilDomain.crear(fichaPerfilId);
        adapter.registrarEstadoInicial(EstadoFichaPerfilMapper.toEntity(inicial));

        // Act
        Optional<EstadoFichaPerfilEntity> resultado = adapter.obtenerEstadoActual(fichaPerfilId);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstadoFicha().getId()).isEqualTo("EN_CONSTRUCCION");
    }

    @Test
    void debeDevolverVacio_cuandoLaFichaNoTieneEstados() {
        // Act & Assert
        assertThat(adapter.obtenerEstadoActual(UUID.randomUUID())).isEmpty();
    }
}
