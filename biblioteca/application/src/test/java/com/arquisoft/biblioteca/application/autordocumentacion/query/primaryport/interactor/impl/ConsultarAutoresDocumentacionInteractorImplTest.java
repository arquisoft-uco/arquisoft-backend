package com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.autordocumentacion.query.criteria.AutorDocumentacionCriteria;
import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model.ConsultarAutoresDocumentacionQuery;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.application.autordocumentacion.query.usecase.ConsultarAutoresDocumentacionUseCase;
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
class ConsultarAutoresDocumentacionInteractorImplTest {

    @Mock
    private ConsultarAutoresDocumentacionUseCase consultarAutoresDocumentacionUseCase;

    @InjectMocks
    private ConsultarAutoresDocumentacionInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoEjecuta() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var query = ConsultarAutoresDocumentacionQuery.crear(documentacion);
        var esperado = List.of(new AutorDocumentacionReadModel(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co"));
        when(consultarAutoresDocumentacionUseCase.ejecutar(new AutorDocumentacionCriteria(documentacion)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarAutoresDocumentacionUseCase).ejecutar(new AutorDocumentacionCriteria(documentacion));
    }
}
