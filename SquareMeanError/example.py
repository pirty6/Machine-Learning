import pandas as pd
import numpy as np
import sklearn
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix, accuracy_score
from sklearn.model_selection import train_test_split

data = pd.read_csv("./winequality-red.csv")

x = data.iloc[:,0:11]
y = data['Reviews']

sc = StandardScaler()
x = sc.fit_transform(x)

pca_new = PCA(n_components=8)
x_new = pca_new.fit_transform(x)

x_train, x_test, y_train, y_test = train_test_split(x_new, y, test_size = 0.25)

lr = LogisticRegression()
lr.fit(x_train, y_train)
lr_predict = lr.predict(x_test)

print(lr_predict)
