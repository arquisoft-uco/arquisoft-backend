package com.arquisoft.biblioteca.application.documentacion.command.usecase.impl;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.AutorDocumentacionOutputPort;
import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.mapper.AutorDocumentacionMapper;
import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.documentacion.command.finder.DocumentacionDelEntregableExisteFinder;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.mapper.DocumentacionMapper;
import com.arquisoft.biblioteca.application.documentacion.command.usecase.RegistrarDocumentacionUseCase;
import com.arquisoft.biblioteca.application.documentacion.command.validator.RegistrarDocumentacionValidator;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.EntregableProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.AutoresDeEntregableFinder;
import com.arquisoft.biblioteca.domain.autordocumentacion.AutorDocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarDocumentacionUseCaseImpl implements RegistrarDocumentacionUseCase {

    private final EntregableProyectoGradoBloqueadoPorIdFinder entregableProyectoGradoBloqueadoPorIdFinder;
    private final DocumentacionDelEntregableExisteFinder documentacionDelEntregableExisteFinder;
    private final BibliotecarioPorIdFinder bibliotecarioPorIdFinder;
    private final AutoresDeEntregableFinder autoresDeEntregableFinder;
    private final RegistrarDocumentacionValidator registrarDocumentacionValidator;
    private final DocumentacionOutputPort documentacionOutputPort;
    private final AutorDocumentacionOutputPort autorDocumentacionOutputPort;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroDocumentacionDomain registro) {
        logger.info(DocumentacionKey.LOG_REGISTRANDO,
                registro.getEntregableProyectoGrado(), registro.getBibliotecario());

        var entregable = entregableProyectoGradoBloqueadoPorIdFinder.obtener(registro.getEntregableProyectoGrado());
        var tieneDocumentacion = documentacionDelEntregableExisteFinder.obtener(registro.getEntregableProyectoGrado());
        var bibliotecario = bibliotecarioPorIdFinder.obtener(registro.getBibliotecario());
        var autores = autoresDeEntregableFinder.obtener(registro.getEntregableProyectoGrado());
        logger.debug(DocumentacionKey.LOG_VERIFICACION_REGISTRO,
                !entregable.esVacio(), tieneDocumentacion,
                !bibliotecario.esVacio() && !bibliotecario.estaEliminado(), autores.size());

        registrarDocumentacionValidator.validar(registro, entregable, tieneDocumentacion, bibliotecario, autores);

        var documentacion = DocumentacionDomain.crear(
                registro.getEntregableProyectoGrado(), registro.getBibliotecario(),
                entregable.getBucket(), entregable.getClaveObjeto());
        var autoresDocumentacion = autores.stream()
                .map(autor -> AutorDocumentacionDomain.crear(autor.autor(), documentacion.getId()))
                .map(AutorDocumentacionMapper::toEntity)
                .toList();

        documentacionOutputPort.registrar(DocumentacionMapper.toEntity(documentacion));
        autorDocumentacionOutputPort.registrarTodos(autoresDocumentacion);

        logger.info(DocumentacionKey.LOG_REGISTRADA,
                documentacion.getId(), registro.getEntregableProyectoGrado(), autores.size());
        return documentacion.getId();
    }
}
