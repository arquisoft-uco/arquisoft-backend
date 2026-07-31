package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Validaciones reutilizables de propiedad y unicidad de ítems de ficha de perfil.
 */
@Component
@RequiredArgsConstructor
public class ItemFichaPerfilValidator {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    /** El estudiante autenticado debe ser propietario de la ficha que contiene el ítem. */
    public void validarFichaPropia(UUID fichaPerfil, UUID estudiante) {
        if (!fichaPerfilQueryOutputPort.esEstudiantePropietario(
                new PropietarioFichaCriteria(fichaPerfil, estudiante))) {
            throw new ItemFichaNoPropiaException(fichaPerfil);
        }
    }

    /** Una ficha no puede tener dos ítems del mismo tipo. */
    public void validarTipoNoDuplicado(UUID fichaPerfil, String tipoItem) {
        if (itemFichaPerfilOutputPort.existePorFichaYTipoItem(fichaPerfil, tipoItem)) {
            throw new ItemTipoDuplicadoException(tipoItem);
        }
    }
}
