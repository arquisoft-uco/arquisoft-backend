package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.DescargarArchivoVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.DescargarArchivoVersionArtefactoUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescargarArchivoVersionArtefactoInteractorImplTest {

    @Mock
    private DescargarArchivoVersionArtefactoUseCase descargarArchivoVersionArtefactoUseCase;

    @InjectMocks
    private DescargarArchivoVersionArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_yRetornarSuResultado() {
        // Arrange
        var query = DescargarArchivoVersionArtefactoQuery.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaVersionArtefacto.ASESOR));
        var esperado = new EnlaceDescargaVersionArtefactoReadModel("http://minio/x", Instant.now());
        when(descargarArchivoVersionArtefactoUseCase.ejecutar(any(DescargaVersionArtefactoCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(descargarArchivoVersionArtefactoUseCase).ejecutar(
                new DescargaVersionArtefactoCriteria(query.versionArtefacto(), query.actor(), true, false));
    }
}
