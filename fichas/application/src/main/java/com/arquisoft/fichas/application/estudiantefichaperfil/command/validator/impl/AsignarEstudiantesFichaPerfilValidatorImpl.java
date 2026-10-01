package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.CupoEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculosEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudianteFichaPerfilCupoDisponibleRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesNoVinculadosRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesSinDuplicadosRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AsignarEstudiantesFichaPerfilValidatorImpl implements AsignarEstudiantesFichaPerfilValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final EstudiantesNoVinculadosRule estudiantesNoVinculadosRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule;

    public AsignarEstudiantesFichaPerfilValidatorImpl() {
        this.estudiantesSinDuplicadosRule = new EstudiantesSinDuplicadosRuleImpl();
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.estudiantesExistenRule = new EstudiantesExistenRuleImpl();
        this.estudiantesNoVinculadosRule = new EstudiantesNoVinculadosRuleImpl();
        this.estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();
        this.estudianteFichaPerfilCupoDisponibleRule = new EstudianteFichaPerfilCupoDisponibleRuleImpl();
    }

    @Override
    public void validar(AgregacionEstudiantesFichaPerfilDomain entrada, FichaPerfilDomain ficha,
                        EstadoFichaPerfilDomain estadoActual, List<UUID> estudiantesExistentes,
                        List<UUID> yaVinculados, long vinculadosActuales) {

        estudiantesSinDuplicadosRule.validar(entrada.getEstudiantes());

        fichaPerfilExisteRule.validar(
                new ExistenciaFichaPerfil(entrada.getFichaPerfil(), !ficha.esVacio()));
        estudiantesExistenRule.validar(
                new ExistenciaEstudiantes(entrada.getEstudiantes(), estudiantesExistentes));
        estudiantesNoVinculadosRule.validar(new VinculosEstudiantesFicha(yaVinculados));

        estadoFichaPerfilEnTerminalRule.validar(
                new EstadoActualFicha(entrada.getFichaPerfil(), estadoActual.getEstadoFicha()));
        estudianteFichaPerfilCupoDisponibleRule.validar(
                new CupoEstudiantesFicha(vinculadosActuales, entrada.getCantidad()));
    }
}
