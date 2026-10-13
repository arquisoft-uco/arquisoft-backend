package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ExistenciaVinculoEstudianteFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.VinculoEstudianteFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RemoverEstudianteFichaPerfilValidatorImpl implements RemoverEstudianteFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    public RemoverEstudianteFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.estudiantesExistenRule = new EstudiantesExistenRuleImpl();
        this.vinculoEstudianteFichaExisteRule = new VinculoEstudianteFichaExisteRuleImpl();
        this.estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();
    }

    @Override
    public void validar(RemocionEstudianteFichaPerfilDomain entrada, boolean fichaExiste,
                        EstadoFichaPerfilDomain estadoActual, List<UUID> estudiantesExistentes,
                        boolean vinculoExiste) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(entrada.getFichaPerfil(), fichaExiste));

        estudiantesExistenRule.validar(
                new ExistenciaEstudiantes(List.of(entrada.getEstudiante()), estudiantesExistentes));

        vinculoEstudianteFichaExisteRule.validar(new ExistenciaVinculoEstudianteFicha(
                entrada.getFichaPerfil(), entrada.getEstudiante(), vinculoExiste));

        estadoFichaPerfilEnTerminalRule.validar(
                new EstadoActualFicha(entrada.getFichaPerfil(), estadoActual.getEstadoFicha()));
    }
}
