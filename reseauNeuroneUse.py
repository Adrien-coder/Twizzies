import tensorflow as tf
import matplotlib.pyplot as plt
import numpy as np
from PIL import Image
from tensorflow.keras.models import load_model
from tensorflow.keras import layers, models

# Charger le modèle
model = load_model('traffic_sign_model.keras')

def imgPrep(filepath):
    print("Ouverture fichier")
    img = Image.open(filepath)
    img = img.resize((32, 32)) # resize image
    img_array = np.array(img)
    img_array = np.expand_dims(img_array, axis=0)  # Ajouter une dimension pour le lot
    return img_array

predictions = model.predict(imgPrep("ref/ref110.jpg"))
print(predictions[0].argmax())