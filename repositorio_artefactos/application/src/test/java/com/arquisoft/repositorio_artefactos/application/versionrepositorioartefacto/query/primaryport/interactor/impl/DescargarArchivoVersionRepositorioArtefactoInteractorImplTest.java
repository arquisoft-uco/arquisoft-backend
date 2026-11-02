package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.ArchivoVersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.DescargarArchivoVersionRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.DescargarArchivoVersionRepositorioArtefactoUseCase;
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
class DescargarArchivoVersionRepositorioArtefactoInteractorImplTest {

    @Mock
    private DescargarArchivoVersionRepositorioArtefactoUseCase useCase;

    @InjectMocks
    private DescargarArchivoVersionRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoSeEjecuta() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var query = DescargarArchivoVersionRepositorioArtefactoQuery.crear(version);
        var esperado = new EnlaceDescargaVersionReadModel("https://s3.test/x", Instant.now());
        when(useCase.ejecutar(new ArchivoVersionRepositorioArtefactoCriteria(version))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(new ArchivoVersionRepositorioArtefactoCriteria(version));
    }
}
