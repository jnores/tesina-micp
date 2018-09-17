package ar.edu.ungs.tesina.micp.example;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ar.edu.ungs.tesina.micp.Instance;
import ar.edu.ungs.tesina.micp.example.model.Aula;
import ar.edu.ungs.tesina.micp.example.model.Clase;
import ar.edu.ungs.tesina.micp.example.runnable.SolveRunnable;
import ar.edu.ungs.tesina.micp.example.ui.MainFrame;

public class MainApp {
	public static final int DEFAULT_PABELLON = 1;
	private Instance<Clase, Aula> mInstance;
	private Properties mProperties;
	private int mPabellon = DEFAULT_PABELLON;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		Options opt = new Options();
		opt.addOption("hola", "Hola Mundo");
		CommandLineParser parser = new DefaultParser();
		boolean isOk = true;
		try {
			CommandLine cmd = parser.parse(opt, args);
			if (cmd.hasOption("hola")) {

			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (!isOk) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("micp", "bla bla bla bla\n", opt, "\nFooter con ejemplo");

			System.exit(1);
		}

		String instancePath = null;
		String aulasPath = null;
		String solutionPath = null;

		if (args.length == 3) {
			instancePath = args[0];
			aulasPath = args[1];
			solutionPath = args[2];
		}

		if (instancePath == null || aulasPath == null || solutionPath == null) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainApp app = new MainApp();
						MainFrame window = new MainFrame(app);
						window.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			try {
				MainApp app = new MainApp();
				int cantAulas = -1;
				try {
					cantAulas = Integer.parseInt(aulasPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cantAulas > 0)
					app.loadInstanceFCEN(instancePath, cantAulas);
				else
					app.loadInstanceFCEN(instancePath, aulasPath);

				app.getRunnable().run();
				app.saveSolution(solutionPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		InitializeProperties();
	}

	private void InitializeProperties() {

		mProperties = new Properties();

		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			mProperties.load(input);

			// get the property value and print it out
			System.out.println(mProperties.getProperty("gap_limit"));
			System.out.println(mProperties.getProperty("time_limit"));
			System.out.println(mProperties.getProperty("pabellon"));

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
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {

	}

	// -------------------------Se obtiene el contenido del
	// Archivo----------------//
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
			System.out.println("# debug - No se creo una instancia porque no hay cursos por asignar.");
		}
		return status;
	}

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

	public Runnable getRunnable() {
		System.out.println("debug - Pabellon: " + mPabellon);
		if (mInstance == null)
			throw new RuntimeException("No se pudo crear la instancia. No hay cursos por asignar");

		return new SolveRunnable(mInstance, mProperties);

	}

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

	public Instance<Clase, Aula> getInstancia() {
		return mInstance;
	}

	// -----------------------------------------------------------------------------//
}
