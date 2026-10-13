package com.arquisoft.fichas.domain.estadofichaperfil.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilNoAprobadaEventTest {

    @Test
    void debeExponerLosDatosYUnaCopiaDeLosEstudiantes_cuandoSeConstruyeElEvento() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesor = new ContactoAsesor("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co");
        var estudiante = new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co");
        var estudiantes = new ArrayList<>(List.of(estudiante));

        // Act
        var evento = new FichaPerfilNoAprobadaEvent(fichaPerfil, "Sistema de gestión", asesor, estudiantes);
        estudiantes.clear();

        // Assert
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Fichas.FICHA_PERFIL_NO_APROBADA);
        assertThat(evento.getTipoEvento()).isEqualTo(FichaPerfilNoAprobadaEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
