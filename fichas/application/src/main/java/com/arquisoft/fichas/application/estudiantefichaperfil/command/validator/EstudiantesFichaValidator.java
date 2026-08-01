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

@Component
@RequiredArgsConstructor
public class EstudiantesFichaValidator {

    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    public void validarSinDuplicados(List<UUID> estudiantes) {
        UtilCollection.firstDuplicate(estudiantes)
                .ifPresent(duplicado -> {
                    throw new EstudianteDuplicadoException(duplicado);
                });
    }

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
