package com.arquisoft.entregables.application.proyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.entregables.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    @Mock
    private AppLogger logger;

    private AgregarProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarProyectoGradoUseCaseImpl(proyectoGradoOutputPort, proyectoGradoExisteFinder, logger);
    }

    @Test
    void debeGuardarYRetornarAgregado_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoExisteFinder.obtener(id)).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(ProyectoGradoDomain.crear(id, "Sistema de gestion"));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionProyectoGradoResult.Agregado.class,
                agregado -> assertThat(agregado.proyectoGrado()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(ProyectoGradoEntity.class);
        verify(proyectoGradoExisteFinder, times(1)).obtener(id);
        verify(proyectoGradoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new ProyectoGradoEntity(id, "Sistema de gestion"));
    }

    @Test
    void debeRetornarDuplicadoSinGuardar_cuandoYaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoExisteFinder.obtener(id)).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(ProyectoGradoDomain.crear(id, "Sistema de gestion"));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionProyectoGradoResult.Duplicado.class,
                duplicado -> assertThat(duplicado.proyectoGrado()).isEqualTo(id));
        verify(proyectoGradoOutputPort, never()).guardar(any());
    }
}
