package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.DescartarEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.DescartarEvaluacionFichaUseCase;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DescartarEvaluacionFichaInteractorImplTest {

    @Mock
    private DescartarEvaluacionFichaUseCase descartarEvaluacionFichaUseCase;

    @InjectMocks
    private DescartarEvaluacionFichaInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCaseConElDominioDelMapper() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var representante = UtilUUID.generarNuevoUUID();
        var command = DescartarEvaluacionFichaCommand.crear(evaluacion, representante);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(descartarEvaluacionFichaUseCase).ejecutar(argThat(entrada ->
                entrada.getEvaluacionFichaPerfil().equals(evaluacion)
                        && entrada.getRepresentanteComite().equals(representante)
                        && entrada.getEstadoEvaluacion() == EstadoEvaluacion.DESCARTADA));
    }
}
