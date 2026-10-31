package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorAbiertaPorVersionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.RegistrarRevisionAsesorValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoEsUltimaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorAbiertaExistenteException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RegistrarRevisionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarRevisionAsesorUseCaseImplTest {

    @Mock
    private VersionArtefactoPorIdFinder versionArtefactoPorIdFinder;
    @Mock
    private ArtefactoPorVersionArtefactoFinder artefactoPorVersionArtefactoFinder;
    @Mock
    private VersionArtefactoEsUltimaFinder versionArtefactoEsUltimaFinder;
    @Mock
    private RevisionAsesorAbiertaPorVersionFinder revisionAsesorAbiertaPorVersionFinder;
    @Mock
    private RegistrarRevisionAsesorValidator validator;
    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarRevisionAsesorUseCaseImpl useCase;

    private UUID asesorId;
    private UUID versionId;
    private RegistroRevisionAsesorDomain registro;
    private VersionArtefactoDomain version;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        asesorId = UtilUUID.generarNuevoUUID();
        versionId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        registro = RegistroRevisionAsesorDomain.crear(RevisionAsesorDomain.crear(versionId), asesorId);
        version = VersionArtefactoDomain.reconstruir(versionId, artefactoId, 3, Instant.now(), "versiones/uri");
        artefacto = ArtefactoDomain.reconstruir(artefactoId, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), asesorId, null);
        when(versionArtefactoPorIdFinder.obtener(versionId)).thenReturn(version);
        when(artefactoPorVersionArtefactoFinder.obtener(versionId)).thenReturn(artefacto);
        when(versionArtefactoEsUltimaFinder.obtener(versionId)).thenReturn(true);
        when(revisionAsesorAbiertaPorVersionFinder.obtener(versionId)).thenReturn(false);
    }

    @Test
    void debeRegistrar_cuandoValido() {
        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        var orden = inOrder(versionArtefactoPorIdFinder, artefactoPorVersionArtefactoFinder,
                versionArtefactoEsUltimaFinder, revisionAsesorAbiertaPorVersionFinder, validator,
                revisionAsesorOutputPort);
        orden.verify(versionArtefactoPorIdFinder).obtener(versionId);
        orden.verify(artefactoPorVersionArtefactoFinder).obtener(versionId);
        orden.verify(versionArtefactoEsUltimaFinder).obtener(versionId);
        orden.verify(revisionAsesorAbiertaPorVersionFinder).obtener(versionId);
        orden.verify(validator).validar(registro, version, artefacto, true, false);
        var captor = ArgumentCaptor.forClass(RevisionAsesorEntity.class);
        orden.verify(revisionAsesorOutputPort).registrar(captor.capture());
        verify(versionArtefactoPorIdFinder, times(1)).obtener(any());
        verify(artefactoPorVersionArtefactoFinder, times(1)).obtener(any());
        verify(versionArtefactoEsUltimaFinder, times(1)).obtener(any());
        verify(revisionAsesorAbiertaPorVersionFinder, times(1)).obtener(any());
        assertThat(resultado).isEqualTo(registro.getRevision().getId());
        assertThat(captor.getValue().id()).isEqualTo(resultado);
        assertThat(captor.getValue().versionArtefacto()).isEqualTo(versionId);
        assertThat(captor.getValue().estado()).isEqualTo(EstadoRevisionAsesor.PENDIENTE.getId());
        assertThat(captor.getValue().fechaCreacion()).isEqualTo(registro.getRevision().getFechaCreacion());
        verify(logger).info(RegistrarRevisionAsesorKey.LOG_REGISTRADA, resultado, 3);
    }

    @Test
    void noDebeRegistrar_cuandoValidatorRechaza() {
        // Arrange
        doThrow(new RevisionAsesorAbiertaExistenteException(versionId, 3)).when(validator)
                .validar(any(), any(), any(), anyBoolean(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(RevisionAsesorAbiertaExistenteException.class);
        verifyNoInteractions(revisionAsesorOutputPort);
        verify(logger, never()).info(eq(RegistrarRevisionAsesorKey.LOG_REGISTRADA), any(), any());
    }
}
