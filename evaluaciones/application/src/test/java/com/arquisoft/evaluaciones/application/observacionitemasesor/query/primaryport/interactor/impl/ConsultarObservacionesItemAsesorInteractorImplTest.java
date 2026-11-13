package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.ConsultarObservacionesItemAsesorUseCase;
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
class ConsultarObservacionesItemAsesorInteractorImplTest {

    @Mock
    private ConsultarObservacionesItemAsesorUseCase useCase;

    @InjectMocks
    private ConsultarObservacionesItemAsesorInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegarEnElUseCase_retornandoLaMismaPagina() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesItemAsesorQuery.crear(
                evaluacion, asesor, ConsultaCriteriaQuery.crear(1, 20, List.of(), null));
        PaginatedResult<ObservacionItemAsesorReadModel> pagina = PaginatedResult.of(List.of(), 1, 20, 0);
        when(useCase.ejecutar(any(ObservacionItemAsesorCriteria.class))).thenReturn(pagina);

        // Act
        var respuesta = interactor.ejecutar(query);

        // Assert
        assertThat(respuesta).isSameAs(pagina);
        var captor = ArgumentCaptor.forClass(ObservacionItemAsesorCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
    }
}
