import pandas as pd
import numpy as np
import sklearn
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler

data = pd.read_csv("./winequality-red.csv")

x = data.iloc[:,:11]

sc = StandardScaler()
x = sc.fit_transform(x)


print(x)
