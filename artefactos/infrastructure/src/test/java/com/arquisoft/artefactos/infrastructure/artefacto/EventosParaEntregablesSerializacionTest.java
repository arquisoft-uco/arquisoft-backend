package com.arquisoft.artefactos.infrastructure.artefacto;

import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoOmitidoEvent;
import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoReactivadoEvent;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.revisionasesor.event.RevisionAsesorCerradaEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.VersionCerradaEvento;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventosParaEntregablesSerializacionTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeSerializarLosCamposQueLeeEntregables_cuandoSeCierraUnaRevision() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var versionRepositorio = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-11-10T10:15:30.123456Z");
        var evento = new RevisionAsesorCerradaEvent("Anteproyecto", 3,
                new VersionCerradaEvento(proyecto, artefacto, version, "pendientes/a.pdf", fecha,
                        versionRepositorio, repositorio, "Repositorio"),
                new com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor("Ana", "ana@uco.edu.co"),
                List.of(new com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante(
                        "Luis", "luis@uco.edu.co")));

        // Act
        var json = mapper.readTree(mapper.writeValueAsString(evento));

        // Assert
        assertThat(json.get("idEvento").asString()).isEqualTo(evento.getIdEvento());
        assertThat(json.has("ocurridoEn")).isTrue();
        assertThat(json.get("documento").asString()).isEqualTo("Anteproyecto");
        assertThat(json.get("numeroVersion").asInt()).isEqualTo(3);
        assertThat(json.get("proyectoGrado").asString()).isEqualTo(proyecto.toString());
        assertThat(json.get("artefacto").asString()).isEqualTo(artefacto.toString());
        assertThat(json.get("versionArtefacto").asString()).isEqualTo(version.toString());
        assertThat(json.get("uriArchivo").asString()).isEqualTo("pendientes/a.pdf");
        assertThat(Instant.parse(json.get("fechaVersion").asString())).isEqualTo(fecha);
        assertThat(json.get("versionRepositorioArtefacto").asString()).isEqualTo(versionRepositorio.toString());
        assertThat(json.get("repositorio").asString()).isEqualTo(repositorio.toString());
        assertThat(json.get("nombreRepositorio").asString()).isEqualTo("Repositorio");
    }

    @Test
    void debeSerializarArtefactoYProyecto_cuandoSeOmiteOSeReactivaUnArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var omitido = new ArtefactoOmitidoEvent("Documento", List.of(new ContactoEstudiante("Ana", "ana@uco.edu.co")),
                new ContactoAsesor("Luis", "luis@uco.edu.co"), artefacto, proyecto);
        var reactivado = new ArtefactoReactivadoEvent(artefacto, proyecto);

        // Act
        var jsonOmitido = mapper.readTree(mapper.writeValueAsString(omitido));
        var jsonReactivado = mapper.readTree(mapper.writeValueAsString(reactivado));

        // Assert
        assertThat(jsonOmitido.get("artefacto").asString()).isEqualTo(artefacto.toString());
        assertThat(jsonOmitido.get("proyectoGrado").asString()).isEqualTo(proyecto.toString());
        assertThat(jsonOmitido.has("ocurridoEn")).isTrue();
        assertThat(jsonReactivado.get("artefacto").asString()).isEqualTo(artefacto.toString());
        assertThat(jsonReactivado.get("proyectoGrado").asString()).isEqualTo(proyecto.toString());
        assertThat(jsonReactivado.get("idEvento").asString()).isEqualTo(reactivado.getIdEvento());
        assertThat(jsonReactivado.has("ocurridoEn")).isTrue();
    }
}
