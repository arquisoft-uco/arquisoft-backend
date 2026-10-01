package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCompaneroCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarCompanerosFichaPerfilQuery;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.ConsultarCompanerosFichaPerfilUseCase;
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
class ConsultarCompanerosFichaPerfilInteractorImplTest {

    @Mock
    private ConsultarCompanerosFichaPerfilUseCase consultarCompanerosFichaPerfilUseCase;

    @Captor
    private ArgumentCaptor<EstudianteFichaPerfilCompaneroCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarCompanerosFichaPerfilInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoSeInvoca() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarCompanerosFichaPerfilQuery.crear(fichaPerfil, estudiante);
        List<EstudianteFichaPerfilReadModel> esperado = List.of();
        when(consultarCompanerosFichaPerfilUseCase.ejecutar(any(EstudianteFichaPerfilCompaneroCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarCompanerosFichaPerfilUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().estudiante()).isEqualTo(estudiante);
    }
}
