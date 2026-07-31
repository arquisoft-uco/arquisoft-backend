package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private EvaluacionFichaPerfilJpaRepository evaluacionFichaPerfilJpaRepository;

    private EvaluacionFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionFichaPerfilQueryOutputAdapter(evaluacionFichaPerfilJpaRepository);
    }

    private UUID persistirEvaluacion(UUID representanteComiteId) {
        var entity = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .representanteComiteId(representanteComiteId)
                .fichaPerfilId(UUID.randomUUID())
                .fechaCreacion(Instant.now())
                .build();
        evaluacionFichaPerfilJpaRepository.save(entity);
        return entity.getId();
    }

    @Test
    void debeRetornarTrue_cuandoRepresentanteEsPropietario() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID evaluacionId = persistirEvaluacion(representanteId);

        // Act & Assert
        assertThat(adapter.esRepresentantePropietario(new PropietarioEvaluacionCriteria(evaluacionId, representanteId))).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoRepresentanteNoEsPropietario() {
        // Arrange
        UUID evaluacionId = persistirEvaluacion(UUID.randomUUID());

        // Act & Assert — otro representante distinto al creador
        assertThat(adapter.esRepresentantePropietario(new PropietarioEvaluacionCriteria(evaluacionId, UUID.randomUUID()))).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoEvaluacionExistePorId() {
        // Arrange
        UUID evaluacionId = persistirEvaluacion(UUID.randomUUID());

        // Act & Assert
        assertThat(adapter.existePorId(evaluacionId)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoEvaluacionNoExistePorId() {
        // Act & Assert
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }
}
