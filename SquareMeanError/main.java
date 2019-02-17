import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

public class main {

	public static void main(String[] args) {
    String filename = "winequality-red.csv";
    String line = null;
    ArrayList<Double[]> sample_aux = new ArrayList<Double[]>();
    ArrayList<Double> y_aux = new ArrayList<Double>();

    try {
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      while((line = bufferedReader.readLine()) != null) {
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

		double[] params = new double[sample_aux.get(1).length];
		double[][] samples = new double[sample_aux.size()][sample_aux.get(1).length];
		double[] y = new double[y_aux.size()];

		for(int i = 0; i < sample_aux.size(); i++) {
			Double[] tmp = sample_aux.get(i);
			for(int j = 0; j < tmp.length; j++) {
				samples[i][j] = (double) tmp[j];
			}
		}

		for(int i = 0; i < y.length; i++) {
			y[i] = (double) y_aux.get(i);
		}


		//Univariate
		/*double[] params = new double[2];
		int[] samples = {1,2,3,4,5};
		double[] y = {2.0,4.0,6.0,8.0,10.0};
		boolean univariate = true;*/

		// double[] params = new double[3];
		// int[][] samples = {{1,1}, {2,2}, {3,3}, {4,4},{5,5}};
		// double[] y = {2.0,4.0,6.0,8.0,10.0};
		boolean univariate = false;

		double[][] b_samples = null;
		double[] old_params = new double[params.length];
		if(univariate) {
			/*b_samples = new double[samples.length][2];
			for(int i = 0; i < samples.length; i++) {
				b_samples[i][0] = 1;
				b_samples[i][1] = samples[i];
			}*/
		} else {
			b_samples = new double[samples.length][samples[0].length+1];
			for(int i = 0; i < samples.length; i++) {
				b_samples[i][0] = 1;
				for(int j = 0; j < samples[0].length; j++) {
					b_samples[i][j+1] = samples[i][j];
				}
			}
		}

		scale(b_samples);
		double alfa = 0.5;
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
		/*System.out.println("Samples:");
		for(int i = 0; i < b_samples.length; i++) {
			for(int j = 0; j < b_samples[0].length; j++) {
				System.out.printf("%.5f ", b_samples[i][j]);
			}
			System.out.println();
		}*/
		System.out.println("Final params:");
		for(int i = 0; i < params.length; i++) {
			System.out.printf("%.5f ", params[i]);
		}
    System.out.println();
	}

	private static void error(double[] params, double[][] b_samples, double[] y) {
		double error_sum = 0.0, hyp, error;
		for(int i = 0; i < b_samples.length; i++) {
			hyp = hyphotesis(params, b_samples[i]);
			System.out.printf("hyp: %.5f y: %.5f\n", hyp, y[i]);
			error = Math.pow(hyp - y[i], 2);
			error_sum += error;
		}
		System.out.printf("Square mean error: %.5f \n", error_sum/b_samples.length);
	}

	public static void scale(double[][] m) {
		double acum = 0.0, max = -10000.0;
		for(int i = 0; i < m.length; i++) {
			for(int j = 1; j < m[0].length; j++) {
				//m[i][j] = (m[i][j] - 1) / m.length;
				acum =+ m[i][j];
				if(m[i][j] > max) {
					max = m[i][j];
				}
			}
		}
		for(int i = 0; i < m.length; i++) {
			for(int j = 1; j < m[0].length; j++) {
				m[i][j] = (m[i][j] - (acum / m.length)) / max;
			}
		}
		/*for(int i = 0; i < m.length; i++) {
			for(int j = 0; j < m[0].length; j++) {
				System.out.printf("%f ", m[i][j]);
			}
			System.out.println();
		}*/
	}

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

	private static double hyphotesis(double[] params, double[] samples) {
		double acum = 0;
		for(int i = 0; i < params.length; i++) {
			acum = acum + params[i] * samples[i];
		}
		//System.out.println(acum);
		return acum;
	}

}
