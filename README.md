## Aplicacion ADN

Esta es una aplicación creada a partir de la especificación dada en la prueba técnica enviada por la empresa Carvajal.

Para este proyecto se implementaron 2 versiones, una de ejecución local y una de ejecución contenerizada por medio de Docker. En este Readme se especificara la implementación local, para ver la implementación contenerizada vea la rama **docker**.

Es este Readme se especificara la forma de desplegar la aplicación en un ambiente local ejecutando de forma independiente cada uno de los componentes, los cuales son:

- Base de datos PostgreSQL
- Backend de Java (Spring Boot)
- Interfaz angular

El punto de entrada para un usuario es un sitio web que está disponible en la dirección: **http://localhost:4200/**

![ADN](https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/local/assets/adn-ui.png)


---

### Requisitos previos

Para ejecutar esta aplicación, debe tener instaladas las siguientes herramientas:

- **PostgreSQL 12.8** u otra versión compatible.
- **Apache Maven 3.6.3** u otra versión compatible.
- **Java 11** configurado en sus variables de entorno del sistema.
- **Angular CLI 13**
- **node v17.0.1** u otra versión compatible, que según la documentación de node son; 12.20.x/14.15.x/16.10.x

---

### ¿Cómo ejecutarlo?

Debe seguir los siguientes pasos en el orden dado.

Primero se deben establecer la base de datos, para esto puede seguir los siguientes pasos:

- Ingrese a su instancia de PostgreSQL con un usuario que cuente con privilegios SUPERUSER y cree un nuevo usuario prueba ejecutando los 2 comandos que se relacionan a continuación;

```
postgres=# CREATE USER prueba WITH PASSWORD 'password';
postgres=# ALTER USER prueba WITH SUPERUSER;
```

- Luego cree la base de datos y asígnele permisos sobre esta al usuario prueba;

```
postgres=# CREATE DATABASE pruebaTecnicaCarvajal;
postgres=# grant all privileges on database pruebaTecnicaCarvajal to prueba;
```
La configuración de la base de datos para en backend puede encontrarla en el archivo: 

- https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/local/adn-app/src/main/resources/application.properties

Ahora ejecute la aplicación Java, para esto debe abrir una terminal y dirigirse a la carpeta adn-app que esta contenida dentro de este proyecto, una vez dentro de adn-app ejecute los siguientes comandos:

```
$ mvn clean package
$ mvn spring-boot:run
```
Luego de finalizado este proceso ya puede realizar peticiones al backend de la aplicación, para esto puede usar la colección de postman que se encuentra en **https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/local/assets/API-REST%20Prueba%20tecnica.postman_collection.json**.

Ejecute la aplicación frondend de este proyecto abriendo una terminal y dirigiéndose a la carpeta adn-ui, una vez dentro ejecute los siguientes comandos

```
$ npm install
$ ng serve
```

Después de esto ya podrá hacer una prueba completa de la aplicación desde cualquier navegador ingresando a **http://localhost:4200/**, para esto por favor tenga en cuenta las siguientes aclaraciones:

- La secuencia de ADN a analizar debe cumplir con el formato especificado en la prueba técnica, esto es; comience y finalice con corchetes, divida las filas con comas, no deje espacios en blanco entre las palabras, cada fila de palabras debe abrir y cerrar con comillas. A continuación puede ver un ejemplo:

```
{"ACTCTATC","CATCTTTC","ATATCTTT","GGCAGTGG","TATCAGGG","TTTTGAGG","GAATGGAC","TGGTTGCT"}
```

- Dado la especificación mostrada en la prueba técnica, para las estadísticas se asumió que la variable **ratio** indica la cantidad de ADN correctos por ADN con mal formaciones; (correctos/defectuosos), por lo cual si la cantidad de secuencias con mal formaciones es cero, este ratio tendrá un valor de cero.

---

#### Notas

Creo que a nivel algorítmico, la clase mas interesante es **https://github.com/carlosNietoTinoco/pruebaTecnica2021-adn/blob/local/adn-app/src/main/java/com/carvajal/adn/service/impl/GraphServiceImpl.java**. En esta clase se implementa la búsqueda de la malformación en la matriz, por medio del método **contieneMalformacionesGeneticas**. Esta búsqueda se hace con una representación de grafos, usando una búsqueda por profundidad limitada iterativa, se puede ver mas sobre este algoritmo en; **https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search**.

Algo muy interesante de esta implementación es que es de orden **O (b ^ d)**, donde **b** es el factor de ramificación y **d** es la profundidad de la meta, esto hace que para el caso en particular donde la profundidad es de 3 (profundidad máxima de exploración en el grafo), este algoritmo tenga un orden lineal, aunque en el peor de los casos este podría acercarse (sin llegar) a un orden **O (n ^ 2)**, que seria cuando el ADN este distribuido siempre en conexiones no lineales de exactamente 4 bases nitrogenadas.

