package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstudianteProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private EstudianteProyectoGradoCommandRepository estudianteProyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private static EstudianteProyectoGradoEntity vinculo(UUID proyectoGrado) {
        return new EstudianteProyectoGradoEntity(UUID.randomUUID(), UUID.randomUUID(), proyectoGrado);
    }

    @Test
    void debeVincularTodosYContarSoloLosDelProyecto_cuandoHayVinculosDeOtroProyecto() {
        // Arrange
        var adapter = new EstudianteProyectoGradoCommandOutputAdapter(estudianteProyectoGradoCommandRepository, logger);
        var proyectoGrado = UUID.randomUUID();
        var vinculos = List.of(vinculo(proyectoGrado), vinculo(proyectoGrado), vinculo(proyectoGrado));
        adapter.vincular(List.of(vinculo(UUID.randomUUID())));

        // Act
        adapter.vincular(vinculos);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.contarPorProyectoGrado(proyectoGrado)).isEqualTo(3);
        var guardado = entityManager.find(EstudianteProyectoGradoJpaEntity.class, vinculos.getFirst().id());
        assertThat(guardado.getEstudianteId()).isEqualTo(vinculos.getFirst().estudiante());
        assertThat(guardado.getProyectoGradoId()).isEqualTo(proyectoGrado);
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_GUARDADOS, proyectoGrado, 3);
    }
}
