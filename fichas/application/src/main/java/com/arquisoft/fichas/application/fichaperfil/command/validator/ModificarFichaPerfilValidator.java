package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.model.TituloFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloDisponibleRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilValidator {

    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final FichaPerfilTituloDisponibleRule fichaPerfilTituloDisponibleRule;

    public void validarPropiedad(UUID fichaPerfil, UUID estudiante) {
        estudiantePropietarioFichaRule.validar(new PropietarioFichaCriteria(fichaPerfil, estudiante));
    }

    public void validarTitulo(FichaPerfilAggregate ficha, String nuevoTitulo) {
        fichaPerfilTituloDisponibleRule.validar(
                new TituloFichaCriteria(ficha.getTituloProyecto(), nuevoTitulo));
    }
}
