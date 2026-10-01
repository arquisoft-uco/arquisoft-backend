package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilEstudianteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaPerfilEstudianteInteractorImplTest {

    @Mock
    private ConsultarEstadosFichaPerfilEstudianteUseCase consultarEstadosFichaPerfilEstudianteUseCase;

    @Captor
    private ArgumentCaptor<EstadoFichaPerfilEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEstadosFichaPerfilEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegarEnUseCase() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarEstadosFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);
        List<EstadoFichaPerfilReadModel> esperado = List.of();
        when(consultarEstadosFichaPerfilEstudianteUseCase.ejecutar(any(EstadoFichaPerfilEstudianteCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstadosFichaPerfilEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().estudiante()).isEqualTo(estudiante);
    }
}
