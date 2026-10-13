package com.arquisoft.fichas.domain.estadofichaperfil.event;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilAgregadoEventTest {

    @Test
    void debeExponerLosDatosYUnaCopiaDeLosEstudiantes_cuandoSeConstruyeElEvento() {
        // Arrange
        var estadoFichaPerfil = UUID.randomUUID();
        var fichaPerfil = UUID.randomUUID();
        var estudiante = new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co");
        var estudiantes = new ArrayList<>(List.of(estudiante));

        // Act
        var evento = new EstadoFichaPerfilAgregadoEvent(estadoFichaPerfil, fichaPerfil, "Sistema de gestión",
                EstadoFicha.DESCARTADA.getId(), EstadoFicha.DESCARTADA.getNombre(), estudiantes);
        estudiantes.clear();

        // Assert
        assertThat(evento.getEstadoFichaPerfilId()).isEqualTo(estadoFichaPerfil);
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstadoFicha()).isEqualTo(EstadoFicha.DESCARTADA.getId());
        assertThat(evento.getEstadoFichaNombre()).isEqualTo(EstadoFicha.DESCARTADA.getNombre());
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Fichas.ESTADO_FICHA_PERFIL_AGREGADO);
        assertThat(evento.getTipoEvento()).isEqualTo(EstadoFichaPerfilAgregadoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
