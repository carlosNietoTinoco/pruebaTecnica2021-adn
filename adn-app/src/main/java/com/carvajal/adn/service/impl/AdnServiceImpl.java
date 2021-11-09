package com.carvajal.adn.service.impl;

import com.carvajal.adn.model.*;
import com.carvajal.adn.repository.GraphStringRepository;
import com.carvajal.adn.repository.ResultDescriptionRepository;
import com.carvajal.adn.repository.ResultRepository;
import com.carvajal.adn.service.AdnService;
import com.carvajal.adn.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdnServiceImpl implements AdnService {

    @Autowired
    private GraphService graphService;

    @Autowired
    private GraphStringRepository graphStringRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private ResultDescriptionRepository resultDescriptionRepository;

    @Override
    public Iterable<GraphString> getAllGraphString() {
        return graphStringRepository.findAll();
    }

    @Override
    @Transactional
    public GraphString saveGraphString(GraphString graphString) {
        return graphStringRepository.save(graphString);
    }

    @Override
    @Transactional
    public Result saveResult(Result result) {
        return resultRepository.save(result);
    }

    @Override
    public EstadisticasDTO generarEstadisticas() {
        Iterable<Result> results = resultRepository.findAll();
        int resultadoDefectuoso = 0;
        int correctos = 0;
        int defectuosos = 0;
        for (Result result:results){
            if (result.getResultDescription().getId() == resultadoDefectuoso)
                defectuosos++;
            else
                correctos++;
        }
        EstadisticasDTO estadisticasDTO = new EstadisticasDTO();
        estadisticasDTO.setCount_correct_dna(correctos);
        estadisticasDTO.setCount_defect_dna(defectuosos);
        System.out.println("valor de correctos" + correctos);
        System.out.println("valor de defectuosos" + defectuosos);
        if (defectuosos > 0)
            estadisticasDTO.setRatio((float)correctos/(float)defectuosos);
        else
            estadisticasDTO.setRatio(0);
        return estadisticasDTO;
    }

    @Override
    @Transactional
    public Boolean procesarAdn(AdnDTO adnDTO){
        boolean contieneMalformacionesGeneticas = graphService.contieneMalformacionesGeneticas(adnDTO);
        Result result = new Result();
        ResultDescription resultDescription;
        if (contieneMalformacionesGeneticas)
            resultDescription = resultDescriptionRepository.findById(0).get();
        else
            resultDescription = resultDescriptionRepository.findById(1).get();
        result.setResultDescription(resultDescription);
        GraphString graphString = new GraphString();
        graphString.setDna(Arrays.asList(adnDTO.getDna()));
        result.setGraphString(graphString);
        saveGraphString(graphString);
        saveResult(result);
        return contieneMalformacionesGeneticas;
    }

    @Override
    public String esUnaSecuenciaValida(AdnDTO adnDTO) {

        String[] filas = adnDTO.getDna();
        if (filas == null || filas.length == 0)
            return BAD_REQUEST;

        Integer tamanoDeLaPrimeraFila = filas[0].length();
        if (tamanoDeLaPrimeraFila == null || tamanoDeLaPrimeraFila == 0)
            return ERROR_EMPY;

        for (String fila:filas) {
            if (fila.length() != tamanoDeLaPrimeraFila)
                return ERROR_TAMANO_MATRIZ;

            //ToDo: Cambiar por expresion regular
            char[] letras = fila.toCharArray();
            String letrasPermitidas = "ATCG";
            int letraNoPermitida = -1;

            for (char letra:letras) {
                if (letrasPermitidas.indexOf(letra) == letraNoPermitida)
                    return ERROR_CONTENIDO;
            }
        }
        return SUCCES;
    }

}
