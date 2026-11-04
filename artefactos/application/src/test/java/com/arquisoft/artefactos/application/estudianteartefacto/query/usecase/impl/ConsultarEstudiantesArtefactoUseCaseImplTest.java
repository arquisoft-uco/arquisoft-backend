package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.EstudianteArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstudiantesArtefactoUseCaseImplTest {

    @Mock
    private EstudianteArtefactoQueryOutputPort estudianteArtefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstudiantesArtefactoUseCaseImpl useCase;

    private static EstudianteArtefactoReadModel readModel(UUID artefacto, String nombre, boolean vigente) {
        return new EstudianteArtefactoReadModel(UtilUUID.generarNuevoUUID(), artefacto,
                UtilUUID.generarNuevoUUID(), "ID-" + nombre, nombre, nombre + "@uco.edu.co", vigente);
    }

    @Test
    void debeRetornarLosReadModels_cuandoHayVinculos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var criteria = new EstudianteArtefactoCriteria(artefacto);
        var esperado = List.of(readModel(artefacto, "Ana", true), readModel(artefacto, "Luis", false),
                readModel(artefacto, "Zoe", true));
        when(estudianteArtefactoQueryOutputPort.consultarPorArtefacto(artefacto)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estudianteArtefactoQueryOutputPort, times(1)).consultarPorArtefacto(artefacto);
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTANDO), eq(artefacto));
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayVinculos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var criteria = new EstudianteArtefactoCriteria(artefacto);
        when(estudianteArtefactoQueryOutputPort.consultarPorArtefacto(artefacto)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
