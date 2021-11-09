package com.carvajal.adn.service;

import com.carvajal.adn.model.AdnDTO;
import com.carvajal.adn.model.EstadisticasDTO;
import com.carvajal.adn.model.GraphString;
import com.carvajal.adn.model.Result;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AdnService {

    public static final String BAD_REQUEST="La variable dna es requerida.";

    public static final String ERROR_CONTENIDO="Las base nitrogenada del ADN solo puede ser representadas por las letras mayusculas A,T,C y G.";

    public static final String ERROR_EMPY="Las filas no pueden ser vacias.";

    public static final String ERROR_TAMANO_MATRIZ="Todas las filas deben tener el mismo tama\u00f1o.";

    public static final String SUCCES="succes";

    Iterable<GraphString> getAllGraphString();

    public GraphString saveGraphString(GraphString graphString);

    @Transactional
    Result saveResult(Result result);

    public EstadisticasDTO generarEstadisticas();

    public Boolean procesarAdn(AdnDTO adnDTO);

    public String esUnaSecuenciaValida(AdnDTO adnDTO);
}
