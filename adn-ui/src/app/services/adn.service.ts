import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Estadisticas } from './../interfaces/estadisticas';
import {Pruebas} from "./../interfaces/pruebas";
import {Adn} from "../interfaces/adn";

@Injectable({
  providedIn: 'root'
})
export class AdnService {

  private api = '/api/';

  constructor(private http: HttpClient) { }

  getEstadisticas() {
    const path = `http://localhost:8080/api/adn/estadisticas/`;
    return this.http.get<Estadisticas>(path);
  }

  getAll(){
    const path = `http://localhost:8080/api/adn/all/`;
    return this.http.get<Pruebas[]>(path);
  }

  enviarAdn(adn: Adn) {
    const path = `http://localhost:8080/api/adn/procesar`;
    return this.http.post(path, adn);
  }

}
