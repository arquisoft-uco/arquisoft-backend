package com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.RegistrarEvaluacionFichaPerfilUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionFichaPerfilInteractorImplTest {

    @Mock
    private RegistrarEvaluacionFichaPerfilUseCase registrarEvaluacionFichaPerfilUseCase;

    @InjectMocks
    private RegistrarEvaluacionFichaPerfilInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCaseYDevolverSuResultado() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var representante = UtilUUID.generarNuevoUUID();
        var command = RegistrarEvaluacionFichaPerfilCommand.crear(ficha, representante);
        var esperado = new InicioEvaluacionFichaPerfilResult.Reactivada(UtilUUID.generarNuevoUUID());
        when(registrarEvaluacionFichaPerfilUseCase.ejecutar(argThat(evaluacion ->
                evaluacion.getFichaPerfilId().equals(ficha)
                        && evaluacion.getRepresentanteComiteId().equals(representante))))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(registrarEvaluacionFichaPerfilUseCase).ejecutar(argThat(evaluacion ->
                evaluacion.getFichaPerfilId().equals(ficha)));
    }
}
