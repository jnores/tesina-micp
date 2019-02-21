# Ejemplo de uso de micp
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

Se implementa la configuración del limite de memoria disponible para el solver.