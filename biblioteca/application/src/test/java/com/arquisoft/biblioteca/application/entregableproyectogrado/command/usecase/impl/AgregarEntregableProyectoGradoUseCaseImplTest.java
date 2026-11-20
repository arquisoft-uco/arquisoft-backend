package com.arquisoft.biblioteca.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.EntregableProyectoGradoExisteFinder;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEntregableProyectoGradoUseCaseImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    @Mock
    private EntregableProyectoGradoExisteFinder entregableProyectoGradoExisteFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarEntregableProyectoGradoUseCaseImpl useCase;

    private final EntregableProyectoGradoDomain entregable = EntregableProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado", "entregables", "a/b.zip", "b.zip");

    @Test
    void debeAgregarYGuardarLaReplica_cuandoElEntregableNoExiste() {
        // Arrange
        when(entregableProyectoGradoExisteFinder.obtener(entregable.getId())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(entregable);

        // Assert
        var orden = inOrder(entregableProyectoGradoExisteFinder, entregableProyectoGradoOutputPort);
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoEntity.class);
        orden.verify(entregableProyectoGradoExisteFinder, times(1)).obtener(entregable.getId());
        orden.verify(entregableProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new EntregableProyectoGradoEntity(entregable.getId(),
                entregable.getProyectoGrado(), 2, "Aprobado", "entregables", "a/b.zip", "b.zip"));
        assertThat(resultado).isEqualTo(new AgregacionEntregableProyectoGradoResult.Agregado(
                entregable.getId(), entregable.getProyectoGrado()));
        verify(logger).debug(EntregableProyectoGradoKey.LOG_VERIFICACION_AGREGAR, entregable.getId(), false);
    }

    @Test
    void debeDescartarSinGuardar_cuandoElEntregableYaFueReplicado() {
        // Arrange
        when(entregableProyectoGradoExisteFinder.obtener(entregable.getId())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(entregable);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEntregableProyectoGradoResult.Descartado(entregable.getId()));
        verify(entregableProyectoGradoOutputPort, never()).guardar(any());
    }
}
