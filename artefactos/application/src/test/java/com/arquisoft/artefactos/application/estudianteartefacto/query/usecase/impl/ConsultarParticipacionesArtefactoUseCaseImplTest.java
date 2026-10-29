package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport.EstudianteArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarParticipacionesArtefactoUseCaseImplTest {

    @Mock
    private EstudianteArtefactoQueryOutputPort estudianteArtefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarParticipacionesArtefactoUseCaseImpl useCase;

    private static ParticipacionArtefactoReadModel readModel(String documento) {
        return new ParticipacionArtefactoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Proyecto", UtilUUID.generarNuevoUUID(), documento, 1, "Repo");
    }

    @Test
    void debeRetornarLosArtefactosDelPuerto_cuandoHayVinculos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var esperado = List.of(readModel("A"), readModel("B"), readModel("C"));
        when(estudianteArtefactoQueryOutputPort.consultarParticipacionesPorEstudiante(estudiante))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(new ParticipacionArtefactoCriteria(estudiante));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estudianteArtefactoQueryOutputPort, times(1)).consultarParticipacionesPorEstudiante(estudiante);
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTANDO_PARTICIPACION), eq(estudiante));
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTA_PARTICIPACION_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayVinculos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(estudianteArtefactoQueryOutputPort.consultarParticipacionesPorEstudiante(estudiante))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new ParticipacionArtefactoCriteria(estudiante));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTA_PARTICIPACION_COMPLETADA), eq(0));
    }
}
