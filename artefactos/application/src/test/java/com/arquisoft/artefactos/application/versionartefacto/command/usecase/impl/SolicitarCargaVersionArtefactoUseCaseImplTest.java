package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.SolicitudCargaEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.SolicitarCargaVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitarCargaVersionArtefactoUseCaseImplTest {

    private static final long MAXIMO = 5_242_880L;

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;
    @Mock
    private EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    @Mock
    private TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    @Mock
    private SolicitarCargaVersionArtefactoValidator validator;
    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private AppLogger logger;

    private SolicitarCargaVersionArtefactoUseCaseImpl useCase;
    private SolicitudCargaVersionArtefactoDomain solicitud;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        useCase = new SolicitarCargaVersionArtefactoUseCaseImpl(artefactoPorIdFinder,
                estudianteArtefactoExisteFinder, tamanoMaximoArchivoFinder, validator, almacenamiento, logger);
        solicitud = SolicitudCargaVersionArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "informe.pdf", "application/pdf", 2048L);
        artefacto = ArtefactoDomain.reconstruir(solicitud.getArtefacto(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    private void prepararFinders() {
        when(artefactoPorIdFinder.obtener(solicitud.getArtefacto())).thenReturn(artefacto);
        when(estudianteArtefactoExisteFinder.obtener(
                new ParticipacionEstudianteArtefacto(solicitud.getSolicitante(), solicitud.getArtefacto())))
                .thenReturn(true);
        when(tamanoMaximoArchivoFinder.obtener()).thenReturn(MAXIMO);
    }

    @Test
    void debeGenerarFormulario_cuandoValidaPasa() {
        // Arrange
        prepararFinders();
        var formulario = new FormularioCarga("http://minio/bucket", Map.of("key", "valor"), Instant.now());
        when(almacenamiento.generarFormularioCarga(any(SolicitudCargaEntity.class))).thenReturn(formulario);

        // Act
        var resultado = useCase.ejecutar(solicitud);

        // Assert
        var orden = inOrder(artefactoPorIdFinder, estudianteArtefactoExisteFinder, tamanoMaximoArchivoFinder,
                validator, almacenamiento);
        orden.verify(artefactoPorIdFinder).obtener(solicitud.getArtefacto());
        orden.verify(estudianteArtefactoExisteFinder).obtener(
                new ParticipacionEstudianteArtefacto(solicitud.getSolicitante(), solicitud.getArtefacto()));
        orden.verify(tamanoMaximoArchivoFinder).obtener();
        orden.verify(validator).validar(solicitud, artefacto, true, MAXIMO);
        var captor = ArgumentCaptor.forClass(SolicitudCargaEntity.class);
        orden.verify(almacenamiento).generarFormularioCarga(captor.capture());
        verify(artefactoPorIdFinder, times(1)).obtener(any());
        verify(estudianteArtefactoExisteFinder, times(1)).obtener(any());
        verify(tamanoMaximoArchivoFinder, times(1)).obtener();
        assertThat(captor.getValue().carga()).isEqualTo(solicitud.getCarga());
        assertThat(captor.getValue().artefacto()).isEqualTo(solicitud.getArtefacto());
        assertThat(captor.getValue().nombreArchivo()).isEqualTo("informe.pdf");
        assertThat(resultado.carga()).isEqualTo(solicitud.getCarga());
        assertThat(resultado.url()).isEqualTo(formulario.url());
        assertThat(resultado.campos()).isEqualTo(formulario.campos());
        assertThat(resultado.expiraEn()).isEqualTo(formulario.expiraEn());
    }

    @Test
    void debeNoGenerarFormulario_cuandoValidatorRechaza() {
        // Arrange
        prepararFinders();
        doThrow(new EstudianteNoPropietarioArtefactoException(
                solicitud.getSolicitante(), solicitud.getArtefacto()))
                .when(validator).validar(any(), any(), anyBoolean(), anyLong());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(solicitud))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
        verifyNoInteractions(almacenamiento);
        verify(logger, never()).info(eq(VersionArtefactoKey.LOG_CARGA_SOLICITADA), any(), any());
    }
}
