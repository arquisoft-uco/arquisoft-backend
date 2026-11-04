package com.arquisoft.mapas_ruta.application.tarea.command.finder.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TareaPorIdFinderImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @InjectMocks
    private TareaPorIdFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var entity = new TareaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), "EN_PROGRESO");
        when(tareaOutputPort.obtenerPorId(entity.id())).thenReturn(Optional.of(entity));

        // Act
        var tarea = finder.obtener(entity.id());

        // Assert
        assertThat(tarea.getId()).isEqualTo(entity.id());
        assertThat(tarea.getEstadoItem()).isEqualTo(EstadoItem.EN_PROGRESO);
        assertThat(tarea.esVacio()).isFalse();
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(tareaOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var tarea = finder.obtener(id);

        // Assert
        assertThat(tarea).isSameAs(TareaDomain.VACIO);
    }
}
