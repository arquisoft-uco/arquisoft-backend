package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule;

    public void validar(FichaPerfilAggregate ficha, List<UUID> estudiantes,
                        List<EstudianteFichaPerfilAggregate> relaciones) {

        estudiantesSinDuplicadosRule.validar(estudiantes);

        asesorFichaExisteRule.validar(ficha.getAsesorFicha());
        fichaPerfilTituloUnicoRule.validar(ficha.getTituloProyecto());
        estudiantesExistenRule.validar(estudiantes);

        if (!relaciones.isEmpty()) {
            estudianteFichaPerfilCupoDisponibleRule.validar(relaciones);
        }
    }
}
