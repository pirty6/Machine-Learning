import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

public class main {
	public static double y[];
	public static double samples[][];

	public static void main(String[] args) {

		// Read file with data
		getInfo("winequality-red-train.csv");

		// Create array of parameters
		double[] params = new double[samples[0].length + 1];

		//Univariate
		/*double[] params = new double[2];
		int[] samples = {1,2,3,4,5};
		double[] y = {2.0,4.0,6.0,8.0,10.0};
		boolean univariate = true;*/

		// double[] params = new double[3];
		// int[][] samples = {{1,1}, {2,2}, {3,3}, {4,4},{5,5}};
		// double[] y = {2.0,4.0,6.0,8.0,10.0};
		boolean univariate = false;

		// Array for the values with the bias
		double[][] b_samples = null;

		// Array to store the parameters
		double[] old_params = new double[params.length];
		if(univariate) {
			/*b_samples = new double[samples.length][2];
			for(int i = 0; i < samples.length; i++) {
				b_samples[i][0] = 1;
				b_samples[i][1] = samples[i];
			}*/
		} else {
			//Add bias 1 to the samples
			b_samples = new double[samples.length][samples[0].length+1];
			for(int i = 0; i < samples.length; i++) {
				b_samples[i][0] = 1;
				for(int j = 0; j < samples[0].length; j++) {
					b_samples[i][j+1] = samples[i][j];
				}
			}
		}

		//Scale the samples using mean and standard deviation
		scale(b_samples);
		double alfa = 0.01;
		int epochs = 0;
		while(true) {
			old_params = Arrays.copyOf(params, params.length);
			params = gradient(params, b_samples, y, alfa);
			error(params, b_samples, y);
			epochs++;
			if(old_params.equals(params) || epochs == 1000) {
				break;
			}
		}
		System.out.println("Final params:");
		for(int i = 0; i < params.length; i++) {
			System.out.printf("%.5f ", params[i]);
		}
    System.out.println();
		System.out.println("Testing...");

		getInfo("winequality-red-test.csv");
		b_samples = new double[samples.length][samples[0].length+1];
		for(int i = 0; i < samples.length; i++) {
			b_samples[i][0] = 1;
			for(int j = 0; j < samples[0].length; j++) {
				b_samples[i][j+1] = samples[i][j];
			}
		}
		scale(b_samples);
		error(params, b_samples, y);
	}







/*FUNCTIONS*/

// Function that gives the square mean error
	private static void error(double[] params, double[][] b_samples, double[] y) {
		double error_sum = 0.0, hyp, error;
		for(int i = 0; i < b_samples.length; i++) {
			hyp = hyphotesis(params, b_samples[i]);
			System.out.printf("hyp: %.5f y: %.5f\n", hyp, y[i]);
			error = Math.pow(hyp - y[i], 2);
			error_sum += error;
		}
		error_sum = (1.0/b_samples.length) * error_sum;
		System.out.printf("Square mean error: %.5f\n", error_sum/b_samples.length);
	}

// Function that scales the data using the mean and the standard deviation
	public static void scale(double[][] m) {
		double acum, mean, s_dev;
		for(int j = 1; j < m[0].length; j++) {
			acum = 0.0;
			for(int i = 0; i < m.length; i++) {
				acum += m[i][j];
			}
			mean = acum / m.length;
			s_dev = 0;
			for(int i = 0; i < m.length; i++) {
				s_dev += Math.pow(m[i][j] - mean, 2);
			}
			s_dev = s_dev * (1.0/m.length);
			s_dev = Math.sqrt(s_dev);
			for(int i = 0; i < m.length; i++) {
				m[i][j] = (m[i][j] - mean) / s_dev;
			}
		}
	}

// Function that does the gradient descent
	public static double[] gradient(double[]params, double[][] samples, double[] y, double alfa) {
		double[] temp = Arrays.copyOf(params, params.length);
		for(int i = 0; i < params.length; i++) {
			double acum = 0, error = 0;
			for(int j = 0; j < samples.length; j++) {
				error = hyphotesis(params, samples[j]) - y[j];
				acum = acum + error * samples[j][i];
			}
			temp[i] = params[i] - alfa * (1.0/samples.length) * acum;
		}
		return temp;
	}

// Function that returns the calulated value using the hyphotesis y=mx+b = x1b + x2T2 + ...
	private static double hyphotesis(double[] params, double[] samples) {
		double acum = 0;
		for(int i = 0; i < params.length; i++) {
			acum = acum + params[i] * samples[i];
		}
		return acum;
	}

// Function that returns the values that are read from the file
	public static void getInfo(String name) {
		String filename = name;
		String line = null;
		ArrayList<Double[]> sample_aux = new ArrayList<Double[]>();
		ArrayList<Double> y_aux = new ArrayList<Double>();

		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {

				//Dont take into account headers
				if(!line.contains("fixed")) {
					String[] stringArray = line.split(",");
					Double[] doubleArray = new Double[stringArray.length - 1];
					for(int i = 0; i < stringArray.length - 1; i++) {
						String numberAsString = stringArray[i];
						doubleArray[i] = Double.parseDouble(numberAsString);
					}
					sample_aux.add(doubleArray);
					y_aux.add(Double.parseDouble(stringArray[stringArray.length - 1]));
				}
			}
			bufferedReader.close();
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to open file");
		} catch (IOException ex) {
			System.out.println("Error reading file");
		}

		samples = new double[sample_aux.size()][sample_aux.get(1).length];
		y = new double[y_aux.size()];

		//Change from Double to double
		for(int i = 0; i < sample_aux.size(); i++) {
			Double[] tmp = sample_aux.get(i);
			for(int j = 0; j < tmp.length; j++) {
				samples[i][j] = (double) tmp[j];
			}
		}

		for(int i = 0; i < y.length; i++) {
			y[i] = (double) y_aux.get(i);
		}
	}

}
