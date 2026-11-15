package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.DescargarDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.DescargarDocumentacionUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarDocumentacionInteractorImplTest {

    @Mock
    private DescargarDocumentacionUseCase descargarDocumentacionUseCase;

    @InjectMocks
    private DescargarDocumentacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoRecibeElQuery() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var query = DescargarDocumentacionQuery.crear(documentacion, List.of(RolConsultaDocumentacion.BIBLIOTECARIO));
        var criteria = new DescargaDocumentacionCriteria(documentacion, true);
        var esperado = new EnlaceDescargaDocumentacionReadModel("https://minio.test/a", Instant.now());
        when(descargarDocumentacionUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(descargarDocumentacionUseCase).ejecutar(criteria);
    }
}
