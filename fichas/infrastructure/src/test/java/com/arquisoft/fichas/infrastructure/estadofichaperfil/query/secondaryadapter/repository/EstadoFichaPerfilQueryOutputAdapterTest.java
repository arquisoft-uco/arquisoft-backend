package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
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
class EstadoFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private EstadoFichaPerfilQueryRepository estadoFichaPerfilRepository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilQueryOutputAdapter(estadoFichaPerfilRepository);

        var estadoFicha = EstadoFichaEntity.builder()
                .id("EN_CONSTRUCCION")
                .nombre("En Construccion")
                .descripcion("Estado inicial")
                .build();
        entityManager.persist(estadoFicha);

        var estadoAprobada = EstadoFichaEntity.builder()
                .id("APROBADA")
                .nombre("Aprobada")
                .descripcion("Estado terminal")
                .build();
        entityManager.persist(estadoAprobada);
    }

    private void persistirEstado(UUID fichaPerfilId, String estadoFichaId, Instant fechaActualizacion) {
        var entity = EstadoFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estadoFicha(entityManager.find(EstadoFichaEntity.class, estadoFichaId))
                .fechaActualizacion(fechaActualizacion)
                .build();
        entityManager.persist(entity);
    }

    @Test
    void debeRetornarEstadoMasReciente_cuandoFichaTieneVariosEstados() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        Instant ahora = Instant.now();
        persistirEstado(fichaPerfilId, "EN_CONSTRUCCION", ahora.minusSeconds(60));
        persistirEstado(fichaPerfilId, "APROBADA", ahora);

        // Act
        Optional<EstadoFichaPerfilEntity> resultado = adapter.obtenerEstadoActual(fichaPerfilId);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstadoFicha().getId()).isEqualTo(EstadoFicha.APROBADA.getId());
    }

    @Test
    void debeRetornarVacio_cuandoFichaNoTieneEstados() {
        // Act
        Optional<EstadoFichaPerfilEntity> resultado = adapter.obtenerEstadoActual(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
