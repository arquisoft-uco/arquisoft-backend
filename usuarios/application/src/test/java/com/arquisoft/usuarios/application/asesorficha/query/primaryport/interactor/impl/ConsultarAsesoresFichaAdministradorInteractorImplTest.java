package com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor.impl;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.usecase.ConsultarAsesoresFichaAdministradorUseCase;
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
class ConsultarAsesoresFichaAdministradorInteractorImplTest {

    @Mock
    private ConsultarAsesoresFichaAdministradorUseCase useCase;

    @InjectMocks
    private ConsultarAsesoresFichaAdministradorInteractorImpl interactor;

    @Test
    void debeMapearLaEntradaYDelegarEnElUseCase_cuandoRecibeUnaConsulta() {
        // Arrange
        var entrada = ConsultaCriteriaQuery.crear(1, 15, List.of(), null);
        var esperado = PaginatedResult.<AsesorFichaReadModel>of(List.of(), 1, 15, 0L);
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(entrada);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AsesorFichaCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(15);
    }
}
