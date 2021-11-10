## Aplicacion ADN

Esta es una aplicación creada a partir de la especificación dada en la prueba técnica enviada por la empresa Carvajal.

Para este proyecto se implementaron 2 versiones, una de ejecución local y una de ejecución contenerizada por medio de Docker. En este Readme se especificara la implementación contenerizada, para ver la implementación local vea la rama [local](https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/tree/local).

Es este Readme se especificara la forma de desplegar la aplicación que está compuesta por 3 contenedores Docker separados que contienen:

- Base de datos PostgreSQL
- Backend de Java (Spring Boot)
- Interfaz angular

El punto de entrada para un usuario es un sitio web que está disponible en la dirección: **http://localhost:4200/**

![ADN](https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/master/assets/adn-ui.png)


---

### Requisitos previos

Para ejecutar esta aplicación, debe instalar dos herramientas: **Docker** y **Docker Compose**.

Instrucciones de cómo instalar **Docker** en [Ubuntu](https://docs.docker.com/install/linux/docker-ce/ubuntu/), [Windows](https://docs.docker.com/docker-for-windows/install/), [Mac](https://docs.docker.com/docker-for-mac/install/).

**Docker Compose** ya está incluido en los paquetes de instalación para *Windows* y *Mac*, por lo que solo los usuarios de Ubuntu deben seguir [estas instrucciones](https://docs.docker.com/compose/install/).

---

### ¿Cómo ejecutarlo?

Toda la aplicación se puede ejecutar con un solo comando en una terminal abierta en la localización de este proyecto:

```
$ docker-compose up
```

En caso que adn-app presente un error de conexión, salga de la ejecución con ctrl+c y vuelva a ejecutar **docker-compose up**, esto se da cuando la aplicación Java esta lista antes que la instancia de PostgreSQL, no se realizo la configuración de espera ya que esto suele ser tratado en el cluster de desligue, como ya se han generado los contenedores este proceso es muy rápido.


Después de esto ya podrá hacer una prueba completa de la aplicación desde cualquier navegador ingresando a **http://localhost:4200/**, tambien puede realizar peticiones al backend de la aplicación, para esto puede usar la colección de postman que se encuentra [aqui](https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/docker/assets/API-REST%20Prueba%20tecnica.postman_collection.json), para esto por favor tenga en cuenta las siguientes aclaraciones:

- La secuencia de ADN a analizar debe cumplir con el formato especificado en la prueba técnica, esto es; comience y finalice con corchetes, divida las filas con comas, no deje espacios en blanco entre las palabras, cada fila de palabras debe abrir y cerrar con comillas. A continuación puede ver un ejemplo:

```
{"ACTCTATC","CATCTTTC","ATATCTTT","GGCAGTGG","TATCAGGG","TTTTGAGG","GAATGGAC","TGGTTGCT"}
```

- Dado la especificación mostrada en la prueba técnica, para las estadísticas se asumió que la variable **ratio** indica la cantidad de ADN correctos por ADN con mal formaciones; (correctos/defectuosos), por lo cual si la cantidad de secuencias con mal formaciones es cero, este ratio tendrá un valor de cero.

---

#### Notas

Creo que a nivel algorítmico, la clase mas interesante es [GraphService.java](https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/docker/adn-app/src/main/java/com/carvajal/adn/service/GraphService.java). En esta clase se implementa la búsqueda de la malformación en la matriz, por medio del método **contieneMalformacionesGeneticas**. Esta búsqueda se hace con una representación de grafos, usando una búsqueda por profundidad limitada iterativa, se puede ver mas sobre este algoritmo en; [Iterative_deepening_depth-first_search](https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search).

Algo muy interesante de esta implementación es que es de orden **O (b ^ d)**, donde **b** es el factor de ramificación y **d** es la profundidad de la meta, esto hace que para el caso en particular donde la profundidad es de 3 (profundidad máxima de exploración en el grafo), este algoritmo tenga un orden lineal, aunque en el peor de los casos este podría acercarse (sin llegar) a un orden **O (n ^ 2)**, que seria cuando el ADN este distribuido siempre en conexiones no lineales de exactamente 4 bases nitrogenadas.

