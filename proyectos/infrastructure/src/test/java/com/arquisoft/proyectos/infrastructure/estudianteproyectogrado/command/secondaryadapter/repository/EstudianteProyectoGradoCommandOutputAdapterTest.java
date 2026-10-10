package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
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

    @Test
    void debeDevolverSoloLosVinculadosAlProyectoIgnorandoOtrosProyectos_cuandoSeConsultaPorVarios() {
        // Arrange
        var adapter = new EstudianteProyectoGradoCommandOutputAdapter(estudianteProyectoGradoCommandRepository, logger);
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var vinculadoAqui = vinculo(proyectoGrado);
        var vinculadoEnOtro = vinculo(otroProyecto);
        var noVinculado = UtilUUID.generarNuevoUUID();
        var ajenoALaConsulta = vinculo(proyectoGrado);
        adapter.vincular(List.of(vinculadoAqui, vinculadoEnOtro, ajenoALaConsulta));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerVinculados(proyectoGrado,
                List.of(vinculadoAqui.estudiante(), vinculadoEnOtro.estudiante(), noVinculado));

        // Assert
        assertThat(resultado).containsExactly(vinculadoAqui.estudiante());
    }

    @Test
    void debeDevolverVacio_cuandoNingunoEstaVinculado() {
        // Arrange
        var adapter = new EstudianteProyectoGradoCommandOutputAdapter(estudianteProyectoGradoCommandRepository, logger);

        // Act
        var resultado = adapter.obtenerVinculados(UtilUUID.generarNuevoUUID(), List.of(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
