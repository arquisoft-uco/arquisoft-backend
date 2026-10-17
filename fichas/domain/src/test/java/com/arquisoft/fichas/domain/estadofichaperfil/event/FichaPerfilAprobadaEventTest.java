package com.arquisoft.fichas.domain.estadofichaperfil.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilAprobadaEventTest {

    @Test
    void debeExponerLosDatosYUnaCopiaDeLosIntegrantes_cuandoSeConstruyeElEvento() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var asesor = new ContactoAsesor("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co");
        var integrante = new IntegranteFicha(UUID.randomUUID(),
                new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"));
        var integrantes = new ArrayList<>(List.of(integrante));

        // Act
        var evento = new FichaPerfilAprobadaEvent(fichaPerfil, "Sistema de gestión", "APROBADA_CON_OBSERVACIONES",
                coordinador, asesor, integrantes);
        integrantes.clear();

        // Assert
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstadoFicha()).isEqualTo("APROBADA_CON_OBSERVACIONES");
        assertThat(evento.getCoordinadorId()).isEqualTo(coordinador);
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getEstudiantes()).containsExactly(integrante);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Fichas.FICHA_PERFIL_APROBADA);
        assertThat(evento.getTipoEvento()).isEqualTo(FichaPerfilAprobadaEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
