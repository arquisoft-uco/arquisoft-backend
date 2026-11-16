package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper.EvaluacionJpaMapper;
import com.arquisoft.shared.util.UtilUUID;
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
    void debePersistirEvaluacion_cuandoEntregableExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        repository.saveAndFlush(EvaluacionJpaMapper.toJpaEntity(
                new EvaluacionEntity(id, entregable, "PENDIENTE")));

        // Assert
        assertThat(repository.findById(id)).hasValueSatisfying(evaluacion -> {
            assertThat(evaluacion.getEntregable()).isEqualTo(entregable);
            assertThat(evaluacion.getEstadoEvaluacion()).isEqualTo("PENDIENTE");
        });
    }

    @Test
    void debeResponderExistencia_porEntregable() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        repository.saveAndFlush(EvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .entregable(entregable)
                .estadoEvaluacion("PENDIENTE")
                .build());

        // Act
        var existente = repository.existsByEntregable(entregable);
        var ausente = repository.existsByEntregable(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existente).isTrue();
        assertThat(ausente).isFalse();
    }

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

    @Test
    void debeLeerEvaluacionYEstado_cuandoLaEvaluacionExisteParaAsignar() {
        // Arrange
        var id = UUID.randomUUID();
        repository.saveAndFlush(EvaluacionJpaEntity.builder()
                .id(id)
                .entregable(UUID.randomUUID())
                .estadoEvaluacion("PENDIENTE")
                .build());

        // Act
        var contexto = repository.buscarContextoAsignacionBloqueado(id);

        // Assert
        assertThat(contexto).hasValueSatisfying(encontrado ->
                assertThat(encontrado.getEstado()).isEqualTo("PENDIENTE"));
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionNoExisteParaAsignar() {
        // Act & Assert
        assertThat(repository.buscarContextoAsignacionBloqueado(UUID.randomUUID())).isEmpty();
    }
}
