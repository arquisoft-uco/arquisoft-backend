package com.arquisoft.entregables.domain.artefacto;

import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccionesArtefactoDomainTest {

    @Test
    void debeCrearRegistro_cuandoLlevaArtefactoYClaveOrigenConEspacios() {
        // Arrange
        var artefacto = ArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Doc",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 1, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "R"));

        // Act
        var registro = RegistroVersionCerradaArtefactoDomain.crear(artefacto, "  origen/clave  ");

        // Assert
        assertThat(registro.getArtefacto()).isSameAs(artefacto);
        assertThat(registro.getClaveOrigen()).isEqualTo("origen/clave");
        assertThatThrownBy(() -> RegistroVersionCerradaArtefactoDomain.crear(null, " "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .contains(EntregablesFields.Artefacto.ID, EntregablesFields.Artefacto.CLAVE_ORIGEN));
    }

    @Test
    void debeCrearOmisionYReactivacion_cuandoDatosValidosYRechazarSiFaltan() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var omision = OmisionArtefactoDomain.crear(artefacto, proyectoGrado, ocurridoEn);
        var reactivacion = ReactivacionArtefactoDomain.crear(artefacto, proyectoGrado, ocurridoEn);

        // Assert
        assertThat(omision.getArtefacto()).isEqualTo(artefacto);
        assertThat(omision.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(reactivacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(omision.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(reactivacion.getArtefacto()).isEqualTo(artefacto);
        assertThat(reactivacion.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThatThrownBy(() -> OmisionArtefactoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .contains(EntregablesFields.Artefacto.ID, EntregablesFields.Artefacto.PROYECTO_GRADO,
                                EntregablesFields.Artefacto.OCURRIDO_EN));
        assertThatThrownBy(() -> ReactivacionArtefactoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeCrearPurgas_cuandoHayProyectoYRechazarSiFalta() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var purgaArtefactos = PurgaArtefactosProyectoGradoDomain.crear(proyecto);
        var purgaPaquetes = PurgaPaquetesProyectoGradoDomain.crear(proyecto);

        // Assert
        assertThat(purgaArtefactos.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(purgaPaquetes.getProyectoGrado()).isEqualTo(proyecto);
        assertThatThrownBy(() -> PurgaArtefactosProyectoGradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class);
    }
}
