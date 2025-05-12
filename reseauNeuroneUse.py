import tensorflow as tf
import matplotlib.pyplot as plt
import numpy as np
from PIL import Image
from tensorflow.keras.models import load_model
from tensorflow.keras import layers, models
from scipy.special import softmax

# Charger le modèle
model = load_model('model.keras')

correspondance={
    0:"20 km/h",
    1:"30 km/h",
    2:"50 km/h",
    3:"60 km/h",
    4:"70 km/h",
    5:"80 km/h",
    6:"Fin 90km/h",
    7:"100 km/h",
    8:"120 km/h",
    9:"Interdit de doubler",
    10:"Interdit de doubler aux camions",
    11:"Attention carrefour",
    12:"Route prioriataire",
    13:"Cedez le passage",
    14:"Stop",
    15:"Véhicules interdits",
    16:"Interdit aux camions",
    17:"Sens interdit",
    18:"Attention",
    19:"Virage à gauche dangereux",
    20:"Virage à droite dangereux",
    21:"Suite de virages dangereux",
    22:"Dos d'ane",
    23:"Route glissante",
    24:"Retecissement",
    25:"Attention chantier",
    26:"Feu tricolore",
    27:"Passage piéton",
    28:"école",
    29:"Vélo",
    30:"Neige",
    31:"Animaux sauvages",
    32:"Autobahn",
    33:"Obligation de tourner à droite",
    34:"Obligation de tourner à gauche",
    35:"Obligation d'aller tout droit",
    36:"Obligation de tourner à droite ou aller tout droit",
    37:"Obligation de tourner à gauche ou aller tout droit",
    38:"Rouler sur la voie de droite",
    39:"Rouler sur la voie de gauche",
    40:"Rond point",
    41:"Fin d'interdiction de dépassement",
    42:"Fin d'interdiction de dépassement pour les camions",
    43:"90 km/h",
    44:"110 km/h",}

def imgPrep1(filepath):
    print("Ouverture fichier")
    img = Image.open(filepath)
    img = img.resize((32, 32)) # resize image
    img_array = np.array(img)
    img_array = np.expand_dims(img_array, axis=0)  # Ajouter une dimension pour le lot
    return img_array

predictions = model.predict(imgPrep1("ref/refdouble.jpg"))
predictions = softmax(predictions, axis=1)
#print(predictions[0])
print("{} à {} %".format(correspondance[int(predictions[0].argmax())],round(float(np.max(predictions[0]*100)),3)))