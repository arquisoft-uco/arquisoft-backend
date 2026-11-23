package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.DestinatariosEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestinatariosEvaluacionCompletosRuleImplTest {

    private static final ContactoCoordinador COORDINADOR =
            new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Coordinador", "coordinador@uco.edu.co");
    private static final ContactoAsesor ASESOR =
            new ContactoAsesor(UtilUUID.generarNuevoUUID(), "Asesor", "asesor@uco.edu.co");
    private static final ContactoEstudiante ESTUDIANTE =
            new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Estudiante", "estudiante@uco.edu.co");

    private final DestinatariosEvaluacionCompletosRuleImpl regla = new DestinatariosEvaluacionCompletosRuleImpl();

    private void assertRechazo(DestinatariosEvaluacionFinalizada entrada, UUID evaluacion) {
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(DestinatariosEvaluacionIncompletosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.Evaluacion.DESTINATARIOS_INCOMPLETOS);
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacion.toString());
                });
    }

    @Test
    void debePermitirFlujo_cuandoHayCoordinadorAsesorYUnEstudianteConCorreo() {
        // Arrange
        var entrada = new DestinatariosEvaluacionFinalizada(
                UtilUUID.generarNuevoUUID(), COORDINADOR, List.of(ESTUDIANTE), ASESOR);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoFaltaElContactoDelCoordinador() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entrada = new DestinatariosEvaluacionFinalizada(
                evaluacion, ContactoCoordinador.VACIO, List.of(ESTUDIANTE), ASESOR);

        // Act & Assert
        assertRechazo(entrada, evaluacion);
    }

    @Test
    void debeLanzar_cuandoElCorreoDelAsesorEstaVacio() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesorSinCorreo = new ContactoAsesor(UtilUUID.generarNuevoUUID(), "Asesor", "");
        var entrada = new DestinatariosEvaluacionFinalizada(
                evaluacion, COORDINADOR, List.of(ESTUDIANTE), asesorSinCorreo);

        // Act & Assert
        assertRechazo(entrada, evaluacion);
    }

    @Test
    void debeLanzar_cuandoNingunEstudianteTieneCorreo() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var sinCorreo = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Estudiante", "");
        var entrada = new DestinatariosEvaluacionFinalizada(evaluacion, COORDINADOR, List.of(sinCorreo), ASESOR);

        // Act & Assert
        assertRechazo(entrada, evaluacion);
    }

    @Test
    void debePermitirFlujo_cuandoAlMenosUnEstudianteTieneCorreo() {
        // Arrange
        var sinCorreo = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Otro", "");
        var entrada = new DestinatariosEvaluacionFinalizada(
                UtilUUID.generarNuevoUUID(), COORDINADOR, List.of(sinCorreo, ESTUDIANTE), ASESOR);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
        assertThat(entrada.estudiantesConCorreo()).containsExactly(ESTUDIANTE);
    }
}
