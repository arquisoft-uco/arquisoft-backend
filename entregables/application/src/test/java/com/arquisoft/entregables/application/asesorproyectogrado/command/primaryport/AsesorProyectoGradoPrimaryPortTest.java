package com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport;

import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.interactor.impl.AgregarAsesorProyectoGradoInteractorImpl;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.interactor.impl.FinalizarAsesorProyectoGradoInteractorImpl;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.mapper.AgregarAsesorProyectoGradoMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.mapper.FinalizarAsesorProyectoGradoMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.model.FinalizarAsesorProyectoGradoCommand;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.AgregarAsesorProyectoGradoUseCase;
import com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.FinalizarAsesorProyectoGradoUseCase;
import com.arquisoft.entregables.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorProyectoGradoPrimaryPortTest {

    @Test
    void debeCrearLosCommands_cuandoLosDatosSonValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var agregar = AgregarAsesorProyectoGradoCommand.crear(asesor.toString(), proyecto.toString(), ocurridoEn);
        var finalizar = FinalizarAsesorProyectoGradoCommand.crear(asesor.toString(), proyecto.toString(), ocurridoEn);

        // Assert
        assertThat(agregar).isEqualTo(new AgregarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));
        assertThat(finalizar).isEqualTo(new FinalizarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoLosDatosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarAsesorProyectoGradoCommand.crear("no-es-uuid", null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.AsesorProyectoGrado.ASESOR,
                                EntregablesFields.AsesorProyectoGrado.PROYECTO_GRADO,
                                EntregablesFields.AsesorProyectoGrado.OCURRIDO_EN));
        assertThatThrownBy(() -> FinalizarAsesorProyectoGradoCommand.crear(null, "x", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.AsesorProyectoGrado.ASESOR,
                                EntregablesFields.AsesorProyectoGrado.PROYECTO_GRADO,
                                EntregablesFields.AsesorProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeMapearElCommandAlDomain_cuandoEsAgregarOFinalizar() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");

        // Act
        var agregar = AgregarAsesorProyectoGradoMapper.toDomain(
                new AgregarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));
        var finalizar = FinalizarAsesorProyectoGradoMapper.toDomain(
                new FinalizarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));

        // Assert
        assertThat(agregar.getAsesor()).isEqualTo(asesor);
        assertThat(agregar.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(agregar.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(finalizar.getAsesor()).isEqualTo(asesor);
        assertThat(finalizar.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(finalizar.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeDelegarEnElUseCaseYDevolverSuResultado_cuandoEjecutanLosInteractores() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");
        var agregarUseCase = Mockito.mock(AgregarAsesorProyectoGradoUseCase.class);
        var finalizarUseCase = Mockito.mock(FinalizarAsesorProyectoGradoUseCase.class);
        var agregada = new AgregacionAsesorProyectoGradoResult.Agregada(asesor, proyecto);
        var finalizada = new FinalizacionAsesorProyectoGradoResult.Finalizada(asesor, proyecto);
        when(agregarUseCase.ejecutar(any())).thenReturn(agregada);
        when(finalizarUseCase.ejecutar(any())).thenReturn(finalizada);

        // Act
        var resultadoAgregar = new AgregarAsesorProyectoGradoInteractorImpl(agregarUseCase)
                .ejecutar(new AgregarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));
        var resultadoFinalizar = new FinalizarAsesorProyectoGradoInteractorImpl(finalizarUseCase)
                .ejecutar(new FinalizarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));

        // Assert
        assertThat(resultadoAgregar).isSameAs(agregada);
        assertThat(resultadoFinalizar).isSameAs(finalizada);
        var captorAgregar = ArgumentCaptor.forClass(AsesorProyectoGradoDomain.class);
        var captorFinalizar = ArgumentCaptor.forClass(AsesorProyectoGradoDomain.class);
        verify(agregarUseCase).ejecutar(captorAgregar.capture());
        verify(finalizarUseCase).ejecutar(captorFinalizar.capture());
        assertThat(captorAgregar.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captorFinalizar.getValue().getProyectoGrado()).isEqualTo(proyecto);
    }
}
