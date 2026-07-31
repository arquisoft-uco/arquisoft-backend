package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.util.UtilCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Validaciones reutilizables sobre la lista de estudiantes de una ficha de perfil.
 *
 * <p>Compartido por el registro de fichas y la asignación posterior de estudiantes,
 * de modo que ambos flujos apliquen exactamente las mismas reglas.</p>
 */
@Component
@RequiredArgsConstructor
public class EstudiantesFichaValidator {

    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    /** Integridad de la lista: ningún estudiante puede repetirse en la misma petición. */
    public void validarSinDuplicados(List<UUID> estudiantes) {
        UtilCollection.firstDuplicate(estudiantes)
                .ifPresent(duplicado -> {
                    throw new EstudianteDuplicadoException(duplicado);
                });
    }

    /** Todos los estudiantes referenciados deben existir. */
    public void validarExistencia(List<UUID> estudiantes) {
        if (UtilCollection.isEmptyOrNull(estudiantes)) {
            return;
        }
        estudiantes.forEach(estudiante -> {
            if (!estudianteQueryOutputPort.existePorId(estudiante)) {
                throw new EstudianteNoEncontradoException(estudiante);
            }
        });
    }

    /** Ningún estudiante puede estar ya vinculado a la ficha. */
    public void validarNoVinculados(UUID fichaPerfil, List<UUID> estudiantes) {
        if (UtilCollection.isEmptyOrNull(estudiantes)) {
            return;
        }
        estudiantes.forEach(estudiante -> {
            if (estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfil, estudiante)) {
                throw new EstudianteDuplicadoException(estudiante);
            }
        });
    }
}
