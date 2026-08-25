package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilCommandOutputAdapterTest {

    @Autowired
    private EstadoFichaPerfilCommandRepository estadoFichaPerfilRepository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilCommandOutputAdapter(estadoFichaPerfilRepository);

        entityManager.persist(EstadoFichaJpaEntity.builder()
                .id("EN_CONSTRUCCION")
                .nombre("En Construccion")
                .descripcion("Estado inicial")
                .build());
        entityManager.persist(EstadoFichaJpaEntity.builder()
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
                adapter.obtenerEstadoActual(fichaPerfilId);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().fichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(resultado.get().estadoFicha()).isEqualTo("EN_CONSTRUCCION");
        assertThat(resultado.get().fechaActualizacion()).isNotNull();
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
        assertThat(resultado.get().estadoFicha()).isEqualTo("EN_CONSTRUCCION");
    }

    @Test
    void debeDevolverVacio_cuandoLaFichaNoTieneEstados() {
        // Act & Assert
        assertThat(adapter.obtenerEstadoActual(UUID.randomUUID())).isEmpty();
    }
}
