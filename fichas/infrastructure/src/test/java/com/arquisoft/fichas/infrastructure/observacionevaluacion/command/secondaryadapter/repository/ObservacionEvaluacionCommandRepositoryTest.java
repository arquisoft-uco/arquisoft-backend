package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ObservacionEvaluacionCommandRepositoryTest {

    private static final String OBSERVACION = "El marco teórico es insuficiente";

    @Autowired
    private ObservacionEvaluacionCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID evaluacionFichaPerfilId;

    @BeforeEach
    void sembrar() {
        evaluacionFichaPerfilId = UtilUUID.generarNuevoUUID();
        entityManager.persist(ObservacionEvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfilId(evaluacionFichaPerfilId)
                .observacion(OBSERVACION)
                .build());
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarTrue_cuandoElParEvaluacionTextoYaExiste() {
        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfilId, OBSERVACION);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElTextoEsDistintoEnLaMismaEvaluacion() {
        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfilId, "Otro texto");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarFalse_cuandoElTextoCoincideEnOtraEvaluacion() {
        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndObservacion(
                UtilUUID.generarNuevoUUID(), OBSERVACION);

        // Assert
        assertThat(existe).isFalse();
    }
}
