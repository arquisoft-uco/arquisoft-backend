package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.ConsultarEstudiantesFichaPerfilUseCase;
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
class ConsultarEstudiantesFichaPerfilInteractorImplTest {

    @Mock
    private ConsultarEstudiantesFichaPerfilUseCase consultarEstudiantesFichaPerfilUseCase;

    @Captor
    private ArgumentCaptor<EstudianteFichaPerfilCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEstudiantesFichaPerfilInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoSeInvoca() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var query = ConsultarEstudiantesFichaPerfilQuery.crear(fichaPerfil);
        List<EstudianteFichaPerfilReadModel> esperado = List.of();
        when(consultarEstudiantesFichaPerfilUseCase.ejecutar(any(EstudianteFichaPerfilCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstudiantesFichaPerfilUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
