package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.ConsultarObservacionesItemAsesorEstudianteUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesItemAsesorEstudianteInteractorImplTest {

    @Mock
    private ConsultarObservacionesItemAsesorEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarObservacionesItemAsesorEstudianteInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegarEnElUseCase_retornandoLaMismaPagina() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesItemAsesorEstudianteQuery.crear(
                evaluacion, estudiante, ConsultaCriteriaQuery.crear(1, 20, List.of(), null));
        PaginatedResult<ObservacionItemAsesorReadModel> pagina = PaginatedResult.of(List.of(), 1, 20, 0);
        when(useCase.ejecutar(any(ObservacionItemAsesorCriteria.class))).thenReturn(pagina);

        // Act
        var respuesta = interactor.ejecutar(query);

        // Assert
        assertThat(respuesta).isSameAs(pagina);
        var captor = ArgumentCaptor.forClass(ObservacionItemAsesorCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(captor.getValue().getEstudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
    }
}
