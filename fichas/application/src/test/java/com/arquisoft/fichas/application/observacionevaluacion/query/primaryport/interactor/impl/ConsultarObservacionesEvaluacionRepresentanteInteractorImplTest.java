package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionRepresentanteQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionRepresentanteUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesEvaluacionRepresentanteInteractorImplTest {

    @Mock
    private ConsultarObservacionesEvaluacionRepresentanteUseCase consultarObservacionesEvaluacionRepresentanteUseCase;

    @InjectMocks
    private ConsultarObservacionesEvaluacionRepresentanteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoQueryEsValido() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionRepresentanteQuery.crear(evaluacionFichaPerfil, representanteComite);
        var criteria = new ObservacionEvaluacionRepresentanteCriteria(evaluacionFichaPerfil, representanteComite);
        var esperado = List.of(new ObservacionEvaluacionReadModel(
                UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil, "Revisar la metodología"));
        when(consultarObservacionesEvaluacionRepresentanteUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarObservacionesEvaluacionRepresentanteUseCase).ejecutar(criteria);
    }
}
