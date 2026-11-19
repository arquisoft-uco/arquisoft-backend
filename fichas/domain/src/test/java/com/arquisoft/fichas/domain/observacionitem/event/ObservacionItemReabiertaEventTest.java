package com.arquisoft.fichas.domain.observacionitem.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemReabiertaEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruyeElEvento() {
        // Arrange
        var observacionItemId = UtilUUID.generarNuevoUUID();
        var revisionItemId = UtilUUID.generarNuevoUUID();
        var fichaPerfilId = UtilUUID.generarNuevoUUID();
        var estudiante = new ContactoEstudiante("Ana Gomez", "ana.gomez@uco.edu.co");

        // Act
        var evento = new ObservacionItemReabiertaEvent(
                observacionItemId, revisionItemId, fichaPerfilId, "Sistema de gestión", List.of(estudiante));

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.observacion_item.reabierta");
        assertThat(evento.getTipoEvento()).isEqualTo("ObservacionItemReabiertaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getObservacionItemId()).isEqualTo(observacionItemId);
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
    }

    @Test
    void debeCopiarListaDeEstudiantes_cuandoLaOriginalCambiaDespues() {
        // Arrange
        var estudiantes = new ArrayList<ContactoEstudiante>();
        estudiantes.add(new ContactoEstudiante("Ana Gomez", "ana.gomez@uco.edu.co"));
        var evento = new ObservacionItemReabiertaEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Sistema de gestión", estudiantes);

        // Act
        estudiantes.add(new ContactoEstudiante("Luis Perez", "luis.perez@uco.edu.co"));

        // Assert
        assertThat(evento.getEstudiantes()).hasSize(1);
    }
}
