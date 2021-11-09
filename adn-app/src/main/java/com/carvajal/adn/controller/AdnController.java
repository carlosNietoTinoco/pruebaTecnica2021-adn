package com.carvajal.adn.controller;

import com.carvajal.adn.model.AdnDTO;
import com.carvajal.adn.service.AdnService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adn")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class AdnController {

    @Autowired
    private AdnService adnService;

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarAdn(@RequestBody AdnDTO adnDTO){
            String esUnaSecuenciaValida = adnService.esUnaSecuenciaValida(adnDTO);
            if (!esUnaSecuenciaValida.equals(adnService.SUCCES))
                return new ResponseEntity<>(
                        esUnaSecuenciaValida,
                        HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(
                    adnService.procesarAdn(adnDTO),
                    HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getGraphString(){
        try {
            return new ResponseEntity<>(
                    adnService.getAllGraphString(),
                    HttpStatus.OK);
        } catch (Exception e) {
            printError(e);
            return errorResponse();
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticas(){
        try {
            return new ResponseEntity<>(
                    adnService.generarEstadisticas(),
                    HttpStatus.OK);
        } catch (Exception e) {
            printError(e);
            return errorResponse();
        }
    }

    private ResponseEntity<String> errorResponse(){
        return new ResponseEntity<>("Se ha producido un error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void printError (Exception e){
        System.out.println("*********************************************");
        System.out.println("EROR:: " + e + " - " + e.toString());
    }
}
