import math
import numpy
import matplotlib.pyplot as plt

__errors__= [];

def h(params, sample):
	"""This evaluates a generic linear function h(x) with current parameters.

	Args:
		params (lst) a list containing the corresponding parameter for each element x of the sample
		sample (lst) a list containing the values of a sample

	Returns:
		Evaluation of h(x)

	"""

	acum = 0
	for i in range(len(params)):
		acum = acum + params[i]*sample[i]  #evaluates h(x) = a+bx1+cx2+ ... nxn..
	acum = acum*(-1);
	acum = 1/(1+ math.exp (acum));
	return acum;


def show_errors(params, samples, y):
	"""Appends the errors that are generated by the estimated values of h and the real value y

	Args:
		params (lst) a list containing the corresponding parameter for each element x of the sample
		samples (lst) a 2 dimensional list containing the input samples
		y (lst) a list containing the corresponding real result for each sample

	"""
	global __errors__
	error_acum =0
	error = 0
#	print("transposed samples")
#	print(samples)
	for i in range(len(samples)):
		hyp = h(params,samples[i])

		if(y[i] == 1): # avoid the log(0) error
			if(hyp ==0):
				hyp = .0001;
			error = (-1)*math.log(hyp);
		if(y[i] == 0):
			if(hyp ==1):
				hyp = .9999;
			error = (-1)*math.log(1-hyp);
		print( "error %f  hyp  %f  y %f " % (error, hyp,  y[i]))
		error_acum=+error # this error is different from the one used to update, this is general for each sentence it is not for each individual param
	#print("acum error %f " % (error_acum));
	mean_error_param=error_acum/len(samples);
	#print("mean error %f " % (mean_error_param));
	__errors__.append(mean_error_param)
	return mean_error_param;

def GD(params, samples, y, alfa):
	"""Gradient Descent algorithm

	Args:
		params (lst) a list containing the corresponding parameter for each element x of the sample
		samples (lst) a 2 dimensional list containing the input samples
		y (lst) a list containing the corresponding real result for each sample
		alfa(float) the learning rate

	Returns:
		temp(lst) a list with the new values for the parameters after 1 run of the sample set

	"""
	temp = list(params)
	general_error=0
	for j in range(len(params)):
		acum =0; error_acum=0
		for i in range(len(samples)):
			error = h(params,samples[i]) - y[i]
			acum = acum + error*samples[i][j]  #Sumatory part of the Gradient Descent formula for linear Regression.
		temp[j] = params[j] - alfa*(1/len(samples))*acum  #Subtraction of original value with learning rate included.
	return temp

def scaling(samples):
	"""Normalizes sample values so that gradient descent can converge

	Args:
		params (lst) a list containing the corresponding parameter for each element x of the sample

	Returns:
		samples(lst) a list with the normalized version of the original samples

	"""
	acum =0
	samples = numpy.asarray(samples).T.tolist()
	for i in range(1,len(samples)):
		for j in range(len(samples[i])):
			acum=+ samples[i][j]
		avg = acum/(len(samples[i]))
		max_val = max(samples[i])
		print("To scale feature %i use (Value -  avg[%f])/ maxval[%f]" % (i, avg, max_val))
		for j in range(len(samples[i])):
			#print(samples[i][j])
			samples[i][j] = (samples[i][j] - avg)/max_val  #Mean scaling
	return numpy.asarray(samples).T.tolist()


#  univariate example
#params = [0,0]
#samples = [1,2,3,4,5,6,7,8,9,11,12,13,14,15,36,47,58,69,100]
#y = [0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1]

#  multivariate example trivial
params = [0,0,0]
samples = [[12,11],[32,21],[43,32],[64,94],[85,75],[64,94],[85,15],[100,94],[85,95],[64,54],[5,5]]
y = [0,0,0,1,1,1,0,1,1,0,0]


#  multivariate example
#params = [0,0,0]
#samples = [[1,1],[2,2],[3,3],[4,4],[5,5],[2,2],[3,3],[4,4]]
#y = [2,4,6,8,10,2,5.5,16]

alfa =.03  #  learning rate
for i in range(len(samples)):
	if isinstance(samples[i], list):
		samples[i]=  [1]+samples[i]
	else:
		samples[i]=  [1,samples[i]]
print ("original samples:")
print (samples)
samples = scaling(samples)
print ("scaled samples:")
print (samples)

while True: # run gradient descent until local minima is reached
	oldparams = list(params)
	print (params)
	params=GD(params, samples,y,alfa)
	error = show_errors(params, samples, y) # only used to show errors, it is not used in calculation
	print (params)
	if(oldparams == params or error < 0.0001): # local minima is found when there is no further improvement or stop when error is 0
		print ("samples:")
		print (samples)
		print ("final params:")
		print (params)
		break
plt.plot(__errors__)
plt.show()
