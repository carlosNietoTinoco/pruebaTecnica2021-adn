import { Component } from '@angular/core';

import {AdnService} from './services/adn.service';
import {Adn} from "./interfaces/adn";
import {Estadisticas} from "./interfaces/estadisticas";
import {Pruebas} from "./interfaces/pruebas";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'adn-ui';

  constructor(
    private adnService: AdnService
  ) {
  }

  estadisticasGet:  Estadisticas = new class implements Estadisticas {
  count_correct_dna=0;
  count_defect_dna=0;
  ratio=0.0;
};

  getEstadisticas() {
    this.adnService.getEstadisticas()
      .subscribe(estadisticas => {
        this.estadisticasGet= estadisticas;
      });
  }
  allPruebas: Pruebas[] | undefined;
  getAll(){
    this.adnService.getAll()
      .subscribe(pruebas => {
        this.allPruebas = pruebas;
      });
  }

  dnaString = "" ;
  resultado: Object = "";


  enviarAdn() {
    let sinCorchetes = this.dnaString.replace(/["{}]/g, '');
    var filas = sinCorchetes.split(",");
    for (var i = 0; i < filas.length; i++) {
      filas[i]=filas[i].trim();
    }
    this.adnService.enviarAdn(new class implements Adn {
      dna: string[]=filas;
    })
      .subscribe(respuesta => {
        this.resultado = respuesta;
      });
  }
}
