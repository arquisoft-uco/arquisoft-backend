package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private EstadoFichaPerfilRepository estadoFichaPerfilRepository;

    @Autowired
    private EstadoFichaRepository estadoFichaRepository;

    private EstadoFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilQueryOutputAdapter(estadoFichaPerfilRepository);

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

    private void persistirEstado(UUID fichaPerfilId, String estadoFichaId, Instant fechaActualizacion) {
        var entity = new EstadoFichaPerfilEntity();
        entity.setId(UUID.randomUUID());
        entity.setFichaPerfilId(fichaPerfilId);
        entity.setEstadoFicha(estadoFichaRepository.findById(estadoFichaId).orElseThrow());
        entity.setFechaActualizacion(fechaActualizacion);
        estadoFichaPerfilRepository.save(entity);
    }

    @Test
    void debeRetornarEstadoMasReciente_cuandoFichaTieneVariosEstados() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        Instant ahora = Instant.now();
        persistirEstado(fichaPerfilId, "EN_CONSTRUCCION", ahora.minusSeconds(60));
        persistirEstado(fichaPerfilId, "APROBADA", ahora);

        // Act
        Optional<EstadoFicha> resultado = adapter.obtenerEstadoActual(fichaPerfilId);

        // Assert
        assertThat(resultado).contains(EstadoFicha.APROBADA);
    }

    @Test
    void debeRetornarVacio_cuandoFichaNoTieneEstados() {
        // Act
        Optional<EstadoFicha> resultado = adapter.obtenerEstadoActual(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
