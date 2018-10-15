package ar.edu.ungs.tesina.micp.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import ar.edu.ungs.tesina.micp.Instance;
import ar.edu.ungs.tesina.micp.MicpScipSolver;
import ar.edu.ungs.tesina.micp.example.model.Aula;
import ar.edu.ungs.tesina.micp.example.model.Clase;

public class MicpApp {
	public static final int DEFAULT_PABELLON = 1;
	private Instance<Clase, Aula> mInstance;
	private Properties mProperties;
	private int mPabellon = DEFAULT_PABELLON;

	/**
	 * Create the application.
	 */
	public MicpApp() {
		this(null);
	}
	
	/**
	 * Create the application.
	 */
	public MicpApp(Properties prop) {
		mProperties = prop;
		
		String propPabellon = mProperties.getProperty("pabellon");
		if (propPabellon == null) {
			mPabellon = DEFAULT_PABELLON;
		} else {
			try {
				int pab = Integer.parseInt(propPabellon);
				mPabellon = pab;
			} catch (Exception ex) {
				mPabellon = DEFAULT_PABELLON;
			}
		}
	}

	public void close() {

	}

	// -------------------- Se obtiene el contenido del Archivo ---------------
	public boolean loadInstanceFCEN(String ruta, int cantAulas) throws FileNotFoundException {
		List<Aula> aulas = new ArrayList<Aula>();
		for (int i = 0; i < cantAulas; i++) {
			aulas.add(new Aula(String.format("%05d", i)));
		}
		return loadInstanceFCEN(ruta, aulas);
	}

	public boolean loadInstanceFCEN(String ruta, String rutaAulas) throws FileNotFoundException {
		List<Aula> aulas = loadAulasFile(rutaAulas);
		return loadInstanceFCEN(ruta, aulas);
	}

	/**
	 * 
	 * 
	 * @param archivo
	 * @param aulas
	 * @return
	 * @throws FileNotFoundException
	 */
	public boolean loadInstanceFCEN(String archivo, List<Aula> aulas) throws FileNotFoundException {

		boolean status = false;
		// Se lee el archivo de texto y se generan los grafos de conflicto y
		// relacion.
		File file = new File(archivo);
		if (!(file.exists() && file.isFile() && file.canRead()))
			throw new RuntimeException(
					"Error de lectura! no se puede abrir el archivo: " + archivo);

		String instanceName = file.getName().split("\\.(?=[^\\.]+$)")[0] + "." + mPabellon;

		FileInputStream fis = new FileInputStream(file);
		Scanner in = new Scanner(fis);

		List<Clase> clases = new ArrayList<Clase>();
		int id = 1;
		int idLinea = 0;
		while (in.hasNextLine()) {
			idLinea++;
			// System.out.println("debug - Leyendo linea "+idLinea);
			Clase clase = null;

			try {
				String linea = in.nextLine();
				if (linea.trim().isEmpty()) {
					System.out.println("debug - Linea vacia: " + idLinea);
				} else {
					try {
						clase = Clase.createClase(id, linea, Clase.Tipo.FCEN);
					} catch (Exception e) {
						System.out.println("debug - Error creando Clase con linea: " + linea);
					}
				}
			} catch (Exception e) {
				System.out.println("debug - Error Lellendo la linea: " + e);
			}

			if (clase != null && (mPabellon == 0 || clase.mPabellon == mPabellon)) {
				clases.add(clase);
				id++;
			} else {
				// if (clase == null)
				// System.out.println("debug - No se instancio clase: ");
				// else
				// System.out.println("debug - No se agrega por ser de otro
				// pabellon: ");
				//
			}
		}

		in.close();
		// Cadena de texto donde se guardara el contenido del archivo

		if (!clases.isEmpty()) {
			Instance<Clase, Aula> instance = new Instance<Clase, Aula>(instanceName, clases, aulas);
			for (int i = 0; i < clases.size(); ++i) {
				for (int j = i + 1; j < clases.size(); ++j) {
					Clase c1 = clases.get(i);
					Clase c2 = clases.get(j);
					if (Clase.seSuperponen(c1, c2))
						instance.addConflicto(c1, c2);
					else if (Clase.igualMateria(c1, c2))
						instance.addRelacion(c1, c2);
				}
			}
			mInstance = instance;
			status = true;
		} else {
			System.out.println("# error - No se pudo parserar el archivo " + instanceName);
			System.out.println(
					"# debug - No se creo una instancia porque no hay cursos por asignar.");
		}
		return status;
	}

	/**
	 * En caso de especificarse un archivo con las aulas disponibles. por ejemplo para mantener la 
	 * nomenclatura, se cara los nombres de las aulas y si se obtiene una asignacion optima, se 
	 * muestran los nombres cargados desde el archivo. 
	 * @param path Ruta del archivo con los nombres de las aulas.
	 * @return Un List con todas las aulas leidas.
	 */
	public List<Aula> loadAulasFile(String path) {
		// Se lee el archivo de texto y se generan los grafos de conflicto y
		// relacion.
		FileReader fr = null;
		BufferedReader br = null;
		// Cadena de texto donde se guardara el contenido del archivo
		Set<Aula> aulas = new HashSet<Aula>();
		try {
			// ruta puede ser de tipo String o tipo File
			fr = new FileReader(path);
			br = new BufferedReader(fr);

			String linea;
			// Obtenemos el contenido del archivo linea por linea
			while ((linea = br.readLine()) != null) {
				if (linea.trim().isEmpty())
					System.out.println("debug - Linea vacia!");
				else {
					aulas.add(new Aula(linea));
				}
			}
		} catch (Exception e) {
		}
		// finally se utiliza para que si todo ocurre correctamente o si ocurre
		// algun error se cierre el archivo que anteriormente abrimos
		finally {
			try {
				br.close();
			} catch (Exception ex) {
			}
		}

		List<Aula> ret = new ArrayList<Aula>();
		ret.addAll(aulas);
		return ret;
	}

	/**
	 * Escribe la asignacion optima encontrada en la ruta pasada por parametro.
	 * @param path Ruta del archivo donde se desea escribir la solucion.
	 * @return 
	 */
	public boolean saveSolution(String path) {

		PrintWriter out = null;

		try {
			out = new PrintWriter(path);
		} catch (FileNotFoundException | SecurityException e) {
			e.printStackTrace();
		}

		if (out != null) {
			out.println("# SOLUCION ENCONTRADA POR EL MICP: ");
			for (Clase c : mInstance.getVertices()) {
				out.println(mInstance.getOptimal(c) + "|" + c.serialize());
			}
		} else {
			System.out.println("======================================== ");
			System.out.println("== No se pudo abrir el archivo de salida ");
			for (Clase c : mInstance.getVertices()) {
				System.out.println(mInstance.getOptimal(c) + "|" + c.serialize());
			}
		}

		return true;
	}

	/**
	 * Getter de la intancia para poder consultarla desde algun Frame.
	 * @return
	 */
	public Instance<Clase, Aula> getInstancia() {
		return mInstance;
	}

	// -----------------------------------------------------------------------------//
	
	
	// ----------------------------- Funciones de resolución ------------------------------------//
	
	/**
	 * genera una instancia runnable para contener la accion de optimización.
	 * Se utiliza para iniciar la optimizacion en un Frame independiente y cancelable.
	 * 
	 * @return objeto Runnable para poder resolverlo en un thread separado y asi poder detenerlo.
	 * 
	 */
	public Runnable getRunnableSolver() {
		System.out.println("debug - Pabellon: " + mPabellon);
		if (mInstance == null)
			throw new RuntimeException("No se pudo crear la instancia. No hay cursos por asignar");

		//return new SolveRunnable(mInstance, mProperties);
		return new Runnable(){

			@Override
			public void run() {
				optimize();
			}
			
		};
	}

	/**
	 * Logica de optimizacion pra ejecutar en el mismo thread que la aplicación. Util en las 
	 * ejecuciones no interactivas como puede ser por linea de comandos.
	 *  
	 */
	public void optimize() {
		MicpScipSolver<Clase,Aula> mMicp = mInstance.createMicp(mProperties);
		try {			
			mInstance.setSolution(		
					mMicp.searchOptimal(
							mInstance.getConflictGraph(),
							mInstance.getRelationshipGraph(),
							mInstance.getAulas()
							)
					);
			mMicp.free();
		} catch (Exception e)
		{
			System.out.println("debug - La optimizacion fue interrumpida!");
			//e.printStackTrace();
			mMicp.free();
			throw new RuntimeException("Optimizacion interrumpida.",e);
		}
	}
}
