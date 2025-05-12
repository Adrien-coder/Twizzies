import matplotlib.pyplot as plt
import csv
from tqdm import tqdm
import tensorflow as tf
from tensorflow.keras import layers, models
import numpy as np
from PIL import Image

def readTrafficSigns(rootpath):
    '''Reads traffic sign data for German Traffic Sign Recognition Benchmark.'''
    images = [] # images
    labels = [] # corresponding labels
    print("Début lecture des fichiers")
    for c in tqdm(range(0, 43)):
        prefix = rootpath + '/' + format(c, '05d') + '/'
        gtFile = open(prefix + 'GT-'+ format(c, '05d') + '.csv')
        gtReader = csv.reader(gtFile, delimiter=';')
        next(gtReader)
        for row in tqdm(gtReader):
            img = Image.open(prefix + row[0])
            img = img.resize((32, 32)) # resize image
            images.append(np.array(img))
            labels.append(int(row[7]))
        gtFile.close()
    return np.array(images), np.array(labels)

def readTrafficSignsTest(rootpath):
    '''Reads traffic sign data for German Traffic Sign Recognition Benchmark.'''
    images = [] # images
    labels = [] # corresponding labels
    print("Début lecture des fichiers test")
    rootpath += '/'
    gtFile = open(rootpath + 'GT-final_test.csv')
    gtReader = csv.reader(gtFile, delimiter=';')
    next(gtReader)
    for row in tqdm(gtReader):
        img = Image.open(rootpath + row[0])
        img = img.resize((32, 32)) # resize image
        images.append(np.array(img))
        labels.append(int(row[7]))
    gtFile.close()
    print('Fin des données test')
    return np.array(images), np.array(labels)

print("Début du programme")
print("Définition du modèle")

model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(32, 32, 3)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.Flatten(),
    layers.Dense(64, activation='relu'),
    layers.Dropout(0.3),
    layers.Dense(43)
])

print("Compilation du modèle")
model.compile(optimizer='adam',
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

print("Import des données")
train_images, train_labels = readTrafficSigns('panneaux/GTSRB/Final_Training/Images')
test_images, test_labels = readTrafficSignsTest('panneaux/GTSRB/Final_Test/Images')

print("Apprentissage")
history = model.fit(train_images, train_labels, epochs=10,
                    validation_data=(test_images, test_labels), verbose=1)

print("Tests")
test_loss, test_acc = model.evaluate(test_images, test_labels, verbose=1)
print(f"Précision sur l'ensemble de test: {test_acc}")

# Exporter le modèle
model.save('traffic_sign_model.keras')
print("Modèle exporté avec succès.")