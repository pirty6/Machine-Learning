import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

public class main {

	public static void main(String[] args) {
		double[] params = new double[3];
		double[][] samples = {{12.0, 11.0}, {31.0,21.0}, {43.0,32.0}, {64.0,94.0}, {85.0,75.0}, {64.0,94.0}, {85.0,15.0}, {100.0,94.0}, {85.0,95.0}, {64.0,54.0}, {5.0,5.0}};
		double[] y = {0,0,0,1,1,1,0,1,1,0,0};

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
		scale(b_samples);
		int epochs = 0;
		double error;
		while(true) {
			old_params = Arrays.copyOf(params, params.length);
			params = gradient(params, b_samples, y, alfa);
			error = error(params, b_samples, y);
			epochs++;
			if(old_params.equals(params) || error < 0.0001) {
				break;
			}
		}
		System.out.println("Final params:");
		for(int i = 0; i < params.length; i++) {
			System.out.printf("%.5f \n", params[i]);
		}
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
	 		for(int i = 0; i < m.length; i++) {
	 			for(int j = 0; j < m[0].length; j++) {
	 				System.out.printf("%f ", m[i][j]);
	 			}
	 			System.out.println();
	 		}
	 	}

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

		public static double hyphotesis(double[] params, double[] samples) {
			double acum = 0.0;
			for(int i = 0; i < params.length; i++) {
				acum = acum + params[i] * samples[i];
			}
			acum = acum * (-1.0);
			acum = 1.0/(1.0 + Math.exp(acum));
			return acum;
		}

		public static double error(double[] params, double[][] b_samples, double[] y) {
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
				System.out.printf("error: %.5f hyp: %.5f y: %.5f\n", error, hyp, y[i]);
				error_sum =+ error;
			}
			error_sum = (1.0/b_samples.length) * error_sum;
			System.out.printf("Mean error: %.5f\n", error_sum/b_samples.length);
			return error_sum/b_samples.length;
		}
}
