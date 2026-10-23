package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.event;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorioArtefactoModificadoEventTest {

    @Test
    void debeCargarRepositorioYNombre_cuandoSeConstruye() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionRepositorioArtefactoDomain.crear(
                repositorio, "Repo Nuevo", "Descripcion", UtilUUID.generarNuevoUUID());

        // Act
        var evento = new RepositorioArtefactoModificadoEvent(modificacion);

        // Assert
        assertThat(evento.getRepositorioArtefacto()).isEqualTo(repositorio);
        assertThat(evento.getNombreRepositorio()).isEqualTo("Repo Nuevo");
    }

    @Test
    void debeUsarTopicDeModificado_cuandoSeConstruye() {
        // Arrange
        var modificacion = ModificacionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "Repo", "Descripcion", UtilUUID.generarNuevoUUID());

        // Act
        var evento = new RepositorioArtefactoModificadoEvent(modificacion);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.RepositorioArtefactos.REPOSITORIO_ARTEFACTO_MODIFICADO);
        assertThat(evento.getTipoEvento()).isEqualTo("RepositorioArtefactoModificadoEvent");
    }
}
