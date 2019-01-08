# tesina-micp
Implementación en java del modelo de programación lineal entera propuesto en el paper "the maximum-impact coloring polytope" de los  investigadores: Mónica Braga, Diego Delle Donne, Rodrigo Linfati y Javier Marenco.

El solver utilizado es [Scip](http://scip.zib.de/) mediante la interfaz [JSCIPOpt](https://github.com/SCIP-Interfaces/JSCIPOpt)

## Dependencias del proyecto
  * Java 8
  * Maven
  * JSCIPOpt
  * jgrapht (mediante maven)
  * jUnit4 (mediante maven)


## Preparacion del entorno
Para poder usar el solver en el proyecto es necesario compilar la interfaz JSCIPOpt.

1. Accedemos al repositorio de  [JSCIPOpt (https://github.com/SCIP-Interfaces/JSCIPOpt)](https://github.com/SCIP-Interfaces/JSCIPOpt) y seguimos las instrucciones. Esto creara una libreria .so y un .jar en build/Release.

2. Una vez compilado y probado se debe registrar la libreria scip.jar en el repositorio local de maven con el siguiente comando:
      ```
      mvn install:install-file -Dfile=scip.jar  -DgroupId=de.zib.scip  -DartifactId=jscipopt -Dversion=1.0.0 -Dpackaging=jar
      ```
3. Por último, agergar la libreria `libjscip.so` al sistema copiandola en `/usr/lib/` en caso de trabajar en linux. Esto nos permitira cargar la libreria dinamica desde Java con `System.loadLibrary("jscip");`

## Ejemplo de uso
En la sección de test se puede encontrar un ejemplo uso de la implementación.

## Estado del desarrollo
Listado de las familias de desigualdades soportadas:
*  Partitioned Inequalities:
    * [x] 2. partitioned inequalities
    * [x] 3. 3-partitioned inequalities
    * [ ] 4. k-partitioned inequalities
* Clique Inequalities:
    * [x] 5. vertex-clique inequalities
    * [x] 6. clique-partitioned inequalities
    * [x] 7. sub-clique inequalities
    * [x] 8. two-color sub-clique inequalities
* Triangle and diamonds:
    * [x] 9. semi-triangle inequalities
    * [x] 10. semi-diamond inequalities
* Valid inequalities:
    * [x] 11. bounding inequalities
    * [x] 12. reinforced bounding inequalities 

## ToDo del ejemplo
* [ ] Agergar configuracion de los parametros del properties desde la interfaz grafica.
    * [x] Agregar menu para habilitar y deshagilitar desigualdades
    * [x] Configurar tiempo maximo de ejecucion.
    * [x] Configurar GAP de corte.
    * [x] Configurar pabellon a cargar del archivo de texto.
* [ ] Setear la configuracion de aulas disponibles al momento de iniciar la optimizacion.

* [ ] Agregar la Pantalla con el detalle de los cursos en conflico y los cursos relacionados.
* [ ] Ver si se pueden pintar los registros de la tabla cuando se selecciona uno. asi algunos aparecen en rojo y otros en verde.