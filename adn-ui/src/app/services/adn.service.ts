import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Estadisticas } from './../interfaces/estadisticas';
import {Pruebas} from "./../interfaces/pruebas";
import {Adn} from "../interfaces/adn";

@Injectable({
  providedIn: 'root'
})
export class AdnService {

  private api = '/api/adn';

  constructor(private http: HttpClient) { }

  getEstadisticas() {
    const path = `${this.api}/estadisticas/`;
    return this.http.get<Estadisticas>(path);
  }

  getAll() {
    const path = `${this.api}/all/`;
    return this.http.get<Pruebas[]>(path);
  }

  enviarAdn(adn: Adn) {
    const path = `${this.api}/procesar`;
    return this.http.post(path, adn);
  }

}
