import tensorflow as tf
from tensorflow.keras import layers, models

# Définir le modèle
model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(32, 32, 3)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.Flatten(),
    layers.Dense(64, activation='relu'),
    layers.Dense(10)  # 10 classes de panneaux de signalisation
])

# Compiler le modèle
model.compile(optimizer='adam',
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

# Afficher le résumé du modèle
model.summary()

# Exemple de chargement des données (à adapter selon votre ensemble de données)
train_images, train_labels = readTrafficSigns('panneaux/GTSRB/Final_Training/Images')
test_images, test_labels = readTrafficSigns('panneaux/GTSRB/Final_Test/Images')

# Entraîner le modèle
history = model.fit(train_images, train_labels, epochs=10,
                    validation_data=(test_images, test_labels))

test_loss, test_acc = model.evaluate(test_images, test_labels, verbose=2)
pr