package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorreccionesRevisionItemEnviadasEventTest {

    private static final ContactoAsesor ASESOR = new ContactoAsesor("Luis Perez", "luis.perez@uco.edu.co");

    @Test
    void debeExponerLosDatos_cuandoSeConstruyeElEvento() {
        // Arrange
        var revisionItemId = UtilUUID.generarNuevoUUID();
        var fichaPerfilId = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new CorreccionesRevisionItemEnviadasEvent(
                revisionItemId, fichaPerfilId, "Sistema de gestión", ASESOR);

        // Assert
        assertThat(evento.getRevisionItemId()).isEqualTo(revisionItemId);
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesor()).isEqualTo(ASESOR);
    }

    @Test
    void debeUsarTopicDeTresSegmentosYAsignarIdYMomento_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new CorreccionesRevisionItemEnviadasEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Sistema de gestión", ASESOR);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("fichas.revision_item.correcciones_enviadas");
        assertThat(evento.getTipoEvento()).isEqualTo("CorreccionesRevisionItemEnviadasEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
