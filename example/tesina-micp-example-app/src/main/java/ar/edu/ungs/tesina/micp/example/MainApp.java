package ar.edu.ungs.tesina.micp.example;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ar.edu.ungs.tesina.micp.example.ui.MainFrame;

public class MainApp {

	private static Options getOptions() {
		Options opts = new Options();

		opts.addOption("x", "Ejecutar resolución sin abrir la interfaz gráfica. Depende de los "
							+ "argumentos -c, -a y -s");
		opts.addOption("c", "cursos", true, "La ruta al archivo que contiene los cursos con los "
											+ "horarios en formato de instancia FCEN");
		opts.addOption("a", "aulas", true, "Puede ser el numero de aulas dispnibles o la ruta al "
											+ "archivo que contiene las aulas disponibles");
		opts.addOption("s", "solucion", true, "La ruta al archivo donde se desea guardar la "
												+ "solución");
		opts.addOption("i", "add-inequality", true, "Agregar desigualdad ##. Se puede repetir el "
													+ "parámetro para agregar más de una "
													+ "desigualdad. Los valores disponibles son el "
													+ "2, el 3 y del 5 al 12 .");
		// agrego las que se superponen con la configuracion por archivo.
		opts.addOption("v", "verbose", false, "Imprimir por STDOUT información detallada de la "
											  + "ejecución del solver. Mediante la interfaz con el "
											  + "solver se le habilita el modo verbose y se "
											  + "imprimen las estadísticas de la ejecución y la "
											  + "mejor solución encontrada.");
		
		opts.addOption("p", "pabellon", true, "Número entero que identifica el pabellon que se "
												+ "debe procesar. Default: 1");
		opts.addOption("g", "gap", true, "Valor de corte del GAP para conciderar una solucion como "
											+ "aceptable, con valores entre 0 y 1. Por ejemplo, para"
											+ " conciderar un gap de 15% se debe poner: '-g 0.15'. "
											+ "Default: 0");
		opts.addOption("m", "memory", true, "Memoria máxima utilizable por el solver en MB."
											+ "Default: 1024");

		opts.addOption("t", "timeout", true, "Tiempo maximo de ejecución del solver en segundos. "
												+ "Default: 3600");
		
		opts.addOption("h", "help", false, "Mostrar este texto de ayuda");
		
		return opts;
	}

	private static CommandLine getCommand(Options opts, String[] args) throws ParseException {
		if (args == null || args.length < 1 || args[0] == null) {
			return null;
		}
		CommandLineParser parser = new DefaultParser();
		return parser.parse(opts, args);
	}

	private static Properties loadProperties() {

		Properties prop = new Properties();

		InputStream input = null;
		String propertiesPath = "config.properties";
		
		File f = new File(propertiesPath);
		if ( !f.exists() || !f.isFile() ) {
			System.out.println( "Error al intentar cargar el archivo de configuracion: " 
								+ f.getAbsolutePath() );
		} else {
			System.out.println("Cargando archivo de configuracion: " + f.getAbsolutePath() );
			try {
	
				input = new FileInputStream(propertiesPath);
	
				// load a properties file
				prop.load(input);			
	
			} catch (IOException ex) {
				System.out.println("Error inesperado cargando el archivo de configuracion: ");
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
		return prop;
	}
	
	/**
	 * Carga los parametros por linea de comandos sobre la instancia properties que se genera a 
	 * partir del arhivo de configuraicon.
	 * 
	 * @param prop
	 * @param cmd
	 * @return Boolean TRUE si el conjunto de parametros es valido. Sino, retorna false.
	 */
	private static boolean loadCliProperties(Properties prop, CommandLine cmd) {
		if (prop == null) 
			prop = new Properties();
		if (cmd == null)
			return true;
		
		
		if ( cmd.hasOption('x') ) {
			if ( cmd.hasOption('c') )
				prop.setProperty("cursos_path", cmd.getOptionValue('c'));
			else
				return false;
			
			if (  cmd.hasOption('a')  )
				prop.setProperty("aulas_path", cmd.getOptionValue('a'));
			else
				return false;
			
			if ( cmd.hasOption('s') )
				prop.setProperty("solucion_path", cmd.getOptionValue('s'));
			else
				return false;
		}
		
		if ( cmd.hasOption('v') ) 
			prop.setProperty("verbose", "true");
		
		// Valido que sea un decimal valido
		if (cmd.hasOption('m'))
		{
			String cliMemory = cmd.getOptionValue('m');
			double memoryValue = -1;
			try {
				memoryValue = Double.parseDouble(cliMemory);
			} catch (Exception e) {
			}
			if (memoryValue < 0) {
				return false;
			} else {
				prop.setProperty("memory_limit", cliMemory);
			}
		}
		
		// Valido que sea un decimal valido
				if (cmd.hasOption('g'))
				{
					String cliGap = cmd.getOptionValue('g');
					double gapValue = -1;
					try {
						gapValue = Double.parseDouble(cliGap);
					} catch (Exception e) {
					}
					if (gapValue < 0) {
						return false;
					} else {
						prop.setProperty("gap_limit", cliGap);
					}
				}
				
		// Valido que sea un entero valido.
		if (cmd.hasOption('t'))
		{
			String cliT = cmd.getOptionValue('t');
			double tValue = -1;
			try {
				tValue = Integer.parseInt(cliT);
			} catch (Exception e) {
			}
			if (tValue < 0) {
				return false;
			} else {
				prop.setProperty("time_limit", cliT);
			}
		}
		
		// Valido que sea un entero valido.
		if (cmd.hasOption('p'))
		{
			String cliP = cmd.getOptionValue('p');
			double pValue = -1;
			try {
				pValue = Integer.parseInt(cliP);
			} catch (Exception e) {
			}
			if (pValue < 0) {
				return false;
			} else {
				prop.setProperty("pabellon", cliP);
			}
		}

		
		if (cmd.hasOption('i'))
		{
			String[] cliIneq = cmd.getOptionValues('i');
			String inequalities = String.join(",",cliIneq);

			prop.setProperty("inequalities_enabled", inequalities);
		}
		
		System.out.println("PROPERTIES: " + getPropertyAsString(prop));
		
		return true;
	}
	
	public static String getPropertyAsString(Properties prop) {    
		  StringWriter writer = new StringWriter();
		  prop.list(new PrintWriter(writer));
		  return writer.getBuffer().toString();
		}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// args = new String[]{"--help"};
		// args = new String[]{"-x","-cpath/to/cursos","-a","24","-s","path/to/solucion","-i2",
		//                     "-i","4" , "-t240", "-g0.05"};
		
		Properties prop = loadProperties();
		Options opts = getOptions();
		boolean isOk = true;
		CommandLine cmd = null;
		// FIXME: Esto devuelve cmd = null y rompe todo!!
		try {
			cmd = getCommand(opts, args);
			isOk = loadCliProperties(prop, cmd);

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		if (!isOk || ( cmd != null && cmd.hasOption('h') ) ) {
			printHelp(opts);
			System.exit(1);
		}
		
		if (cmd != null && cmd.hasOption('x')) {
			processCli(prop);
		} else {
			loadGui(prop);
		}
	}
	
	/**
	 * Pinta la ayuda de la aplicacion.
	 * @param opts
	 */
	private static void printHelp(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(
				// Usage example
				"micp [OPTIONS]...",
				// Header
				"La aplicacion cuenta por defecto, con una interfaz gráfica para operar. También, "
				+ "dispone de una mecanismo de uso por consola especificando los parametros de "
				+ "entrada: --aulas --cursos y --solucion.",
				// Options List
				opts,
				// Footer
				"\nPara realizar un procesamiento por consola se podría ejecutar la aplicacion de "
				+ "la siguiente manera:"
				+ "\n    java -jar micp.jar -x -a20 -c /ruta/a/instancia_file -s instancia_20.sol -d2 -d4");
	}

	/**
	 * Ejecuta la secuencia de la aplicacion sin interfaz gracica, usando los parametros 
	 * obligarorios: cursos, aulas y solucion.
	 * @param prop
	 */
	private static void processCli(Properties prop) {
		String instancePath = prop.getProperty("cursos_path", "");
		String aulasPath = prop.getProperty("aulas_path", "");
		String solutionPath = prop.getProperty("solucion_path", "");
		
		try {
			MicpApp app = new MicpApp(prop);
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

			app.optimize();
			app.saveSolution(solutionPath);
		} catch (FileNotFoundException e) {
			System.out.println("error - No se pudo leer la instancia de archivo.");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Inicia la interfaz grafica de la palicacion. Con las ocnfiguraciones pasadas por parametro y
	 * por archivo de configuración.
	 * 
	 * @param prop Instancia de Properties con las cofiguraciones recibidas.
	 */
	private static void loadGui(Properties prop) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MicpApp app = new MicpApp(prop);
					MainFrame window = new MainFrame(app);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
