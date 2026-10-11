package com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoficha.query.criteria.EstadoFichaCriteria;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.application.estadoficha.query.usecase.ConsultarEstadosFichaUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaInteractorImplTest {

    @Mock
    private ConsultarEstadosFichaUseCase consultarEstadosFichaUseCase;

    @Captor
    private ArgumentCaptor<EstadoFichaCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEstadosFichaInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConLosRolesDelQuery_yRetornarSuResultado() {
        // Arrange
        var query = ConsultarEstadosFichaQuery.crear(List.of("COORDINADOR", "REPRESENTANTE_COMITE"));
        var resultadoEsperado = List.of(new EstadoFichaReadModel("APROBADA", "Aprobada", "Ficha aprobada"));
        when(consultarEstadosFichaUseCase.ejecutar(any(EstadoFichaCriteria.class))).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarEstadosFichaUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().roles()).containsExactly("COORDINADOR", "REPRESENTANTE_COMITE");
    }
}
