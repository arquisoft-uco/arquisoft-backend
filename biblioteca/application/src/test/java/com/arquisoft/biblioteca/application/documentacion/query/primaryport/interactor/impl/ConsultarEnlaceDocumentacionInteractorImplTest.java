package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarEnlaceDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarEnlaceDocumentacionUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEnlaceDocumentacionInteractorImplTest {

    @Mock
    private ConsultarEnlaceDocumentacionUseCase consultarEnlaceDocumentacionUseCase;

    @InjectMocks
    private ConsultarEnlaceDocumentacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var query = ConsultarEnlaceDocumentacionQuery.crear(documentacion);
        var criteria = new EnlaceDocumentacionCriteria(documentacion);
        var esperado = new EnlaceDescargaDocumentacionReadModel("https://minio.test/a", Instant.now());
        when(consultarEnlaceDocumentacionUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEnlaceDocumentacionUseCase).ejecutar(criteria);
    }
}
