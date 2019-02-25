import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

public class main {
	public static double y[];
	public static double samples[][];

	public static void main(String[] args) {
		//Get the values from the file train.csv and adds them to the y and samples array
		getInfo("train.csv");
		double[] params = new double[samples[0].length + 1];
		// double[] params = new double[3];
		// double[][] samples = {{12.0, 11.0}, {31.0,21.0}, {43.0,32.0}, {64.0,94.0}, {85.0,75.0}, {64.0,94.0}, {85.0,15.0}, {100.0,94.0}, {85.0,95.0}, {64.0,54.0}, {5.0,5.0}};
		// double[] y = {0,0,0,1,1,1,0,1,1,0,0};

		double alfa = 0.03;
		double[][] b_samples = null;
		double[] old_params = new double[params.length];
		b_samples = new double[samples.length][samples[0].length+1];
		for(int i = 0; i < samples.length; i++) {
			b_samples[i][0] = 1;
			for(int j = 0; j < samples[0].length; j++) {
				b_samples[i][j+1] = samples[i][j];
			}
		}

		int epochs = 0;
		double error;
		while(true) {
			old_params = Arrays.copyOf(params, params.length);
			params = gradient(params, b_samples, y, alfa);
			error = error(params, b_samples, y, 0);
			epochs++;
			if(old_params.equals(params) || error < 0.0001 || epochs == 10000) {
				break;
			}
		}
		System.out.println("Final params:");
		for(int i = 0; i < params.length; i++) {
			System.out.printf("%.5f ", params[i]);
		}
		System.out.println();
		//Test the model using the parameters from training
		System.out.println("Testing...");
		getInfo("test.csv");
		b_samples = new double[samples.length][samples[0].length+1];
		for(int i = 0; i < samples.length; i++) {
			b_samples[i][0] = 1;
			for(int j = 0; j < samples[0].length; j++) {
				b_samples[i][j+1] = samples[i][j];
			}
		}
		error(params, b_samples, y , 1);
   }



// Function that returns the new parameters by minimizing the cost using gradient descent
		public static double[] gradient(double[] params, double[][] samples, double[] y, double alfa) {
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

// Function to return the predicted value using the sigmoid Function
		public static double hyphotesis(double[] params, double[] samples) {
			double acum = 0.0;
			for(int i = 0; i < params.length; i++) {
				acum = acum + params[i] * samples[i];
			}
			acum = acum * (-1.0);
			acum = 1.0/(1.0 + Math.exp(acum));
			return acum;
		}

	// Function to return the mean error of the model using cross entropy, when
	//test = 1 then it will print discrete values if not it will print them as a
	//continuos value between 1 and 0
		public static double error(double[] params, double[][] b_samples, double[] y, int test) {
			double error_sum = 0.0, hyp, error = 0.0;
			for(int i = 0; i < b_samples.length; i++) {
				hyp = hyphotesis(params, b_samples[i]);
				if(y[i] == 1) {
					if(hyp == 0) {
						hyp = 0.0001;
					}
					error = (-1.0) * Math.log(hyp);
				}
				if(y[i] == 0) {
					if(hyp == 1) {
						hyp = 0.9999;
					}
					error = (-1.0) * Math.log(1.0-hyp);
				}
				if(test == 1) {
					if(hyp <= 0.5) {
						hyp = 0.0;
					} else if(hyp > 0.5) {
						hyp = 1.0;
					}
				}
				System.out.printf("error: %.5f hyp: %.5f y: %.5f\n", error, hyp, y[i]);
				error_sum =+ error;
			}
			error_sum = (1.0/b_samples.length) * error_sum;
			System.out.printf("Mean error: %.5f\n", error_sum);
			return error_sum;
		}

// Function to get the values from files
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
					if(!line.contains("class")) {
						String[] stringArray = line.split(",");
						Double[] doubleArray = new Double[stringArray.length - 1];
						for(int i = 0; i < stringArray.length - 1; i++) {
							// Change text into numbers
							if("femaleSCQ ".contains(stringArray[i])) {
								if(stringArray[i].equals("female")) {
									doubleArray[i] = 1.0;
								} else if (stringArray[i].equals("male")) {
									doubleArray[i] = 0.0;
								}
								if (stringArray[i].equals("S")) {
									doubleArray[i] = 0.0;
								} else if (stringArray[i].equals("C")) {
									doubleArray[i] = 1.0;
								} else if(stringArray[i].equals("Q")) {
									doubleArray[i] = 2.0;
								}
								if(stringArray[i].equals(" ")) {
									doubleArray[i] = 0.0;
								}
								if(stringArray[i].equals(null)) {
									doubleArray[i] = 0.0;
								}
							} else if(i == 2) {
								//Make groups of age
								String numberAsString = stringArray[i];
								double aux = Double.parseDouble(numberAsString);
								if(aux <= 16.0) {
									doubleArray[i] = 0.0;
								} else if(aux > 16 && aux <= 32) {
									doubleArray[i] = 1.0;
								} else if(aux > 32 && aux <= 48) {
									doubleArray[i] = 2.0;
								} else if(aux > 48 && aux <= 64) {
									doubleArray[i] = 3.0;
								} else {
									doubleArray[i] = 4.0;
								}
							}else if(i == 3) {
								//Make groups based on the fare
								String numberAsString = stringArray[i];
								double aux = Double.parseDouble(numberAsString);
								if(aux <= 7.91) {
									doubleArray[i] = 0.0;
								} else if(aux > 7.91 && aux <= 14.454) {
									doubleArray[i] = 1.0;
								} else if(aux > 14.454 && aux <= 31) {
									doubleArray[i] = 2.0;
								} else if(aux > 31) {
									doubleArray[i] = 3.0;
								}
							}else {
								String numberAsString = stringArray[i];
								doubleArray[i] = Double.parseDouble(numberAsString);
							}
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

			System.out.println(sample_aux.size());
			samples = new double[sample_aux.size()][sample_aux.get(1).length];
			y = new double[y_aux.size()];

			//Change from Double to double
			for(int i = 0; i < sample_aux.size(); i++) {
				Double[] tmp = sample_aux.get(i);
				System.out.println(Arrays.toString(tmp));
				for(int j = 0; j < tmp.length; j++) {
					if(tmp[j] == null) {
						samples[i][j] = 1.0;
					} else {
						samples[i][j] = (double) tmp[j];
					}
				}
			}

			for(int i = 0; i < y.length; i++) {
				y[i] = (double) y_aux.get(i);
			}
		}

}
