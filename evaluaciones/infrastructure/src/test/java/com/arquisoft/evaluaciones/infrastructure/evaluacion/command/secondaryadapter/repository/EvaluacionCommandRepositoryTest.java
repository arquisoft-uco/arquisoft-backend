package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class EvaluacionCommandRepositoryTest {

    @Autowired
    private EvaluacionCommandRepository repository;

    @Test
    void debeActualizarSoloElEstado_cuandoLaEvaluacionExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        repository.saveAndFlush(EvaluacionJpaEntity.builder()
                .id(id)
                .entregable(entregable)
                .estadoEvaluacion("PENDIENTE")
                .build());

        // Act
        var filas = repository.actualizarEstado(id, "EN_PROGRESO");

        // Assert
        assertThat(filas).isEqualTo(1);
        assertThat(repository.findById(id)).hasValueSatisfying(evaluacion -> {
            assertThat(evaluacion.getEstadoEvaluacion()).isEqualTo("EN_PROGRESO");
            assertThat(evaluacion.getEntregable()).isEqualTo(entregable);
        });
    }
}
