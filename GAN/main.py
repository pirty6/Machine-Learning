import time
import os
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
import numpy as np

from keras.models import Sequential
from keras.layers import Conv2D, Conv2DTranspose, Reshape
from keras.layers import Flatten, BatchNormalization, Dense, Activation
from keras.layers.advanced_activations import LeakyReLU
from keras.optimizers import Adam
from keras.preprocessing.image import ImageDataGenerator
from keras.regularizers import l1_l2


# Path where the files are located
DATASET_PATH = './gdrive/My Drive/App/images/'

# Wantes epoch size
BATCH_SIZE = 64

# Wanted number of epochs
EPOCHS = 175

# Wanted image shape
IMAGE_SHAPE = (64, 64, 3)

# Total of images located in the files
NO_IMAGES = 3560

# Regularizator
reg = lambda: l1_l2(l1=1e-7, l2=1e-7)
final_generated_images = None

# Function that loads the dataset using keras ImageDataGenerator
def load_dataset(dataset_path, batch_size, image_shape):
    dataset_generator = ImageDataGenerator(horizontal_flip=True)
    train_generator = dataset_generator.flow_from_directory(
        dataset_path, target_size=(image_shape[0], image_shape[1]),
        batch_size=batch_size,
        class_mode=None,
        subset='training')
    return train_generator


# Creates the discriminator model. This model tries to classify images as real
# or fake.
def construct_discriminator(image_shape):

    discriminator = Sequential()
    discriminator.add(Conv2D(filters=64, kernel_size=(5, 5),
                             strides=(2, 2), padding='same',
                             data_format='channels_last',
                             kernel_initializer='glorot_uniform',
                             input_shape=(image_shape)))
    discriminator.add(LeakyReLU(0.2))

    discriminator.add(Conv2D(filters=128, kernel_size=(5, 5),
                             strides=(2, 2), padding='same',
                             data_format='channels_last',
                             kernel_initializer='glorot_uniform'))
    discriminator.add(BatchNormalization(momentum=0.5))
    discriminator.add(LeakyReLU(0.2))

    discriminator.add(Conv2D(filters=256, kernel_size=(5, 5),
                             strides=(2, 2), padding='same',
                             data_format='channels_last',
                             kernel_initializer='glorot_uniform'))
    discriminator.add(BatchNormalization(momentum=0.5))
    discriminator.add(LeakyReLU(0.2))

    discriminator.add(Conv2D(filters=512, kernel_size=(5, 5),
                             strides=(2, 2), padding='same',
                             data_format='channels_last',
                             kernel_initializer='glorot_uniform'))
    discriminator.add(BatchNormalization(momentum=0.5))
    discriminator.add(LeakyReLU(0.2))

    discriminator.add(Flatten())
    discriminator.add(Dense(1))
    discriminator.add(Activation('sigmoid'))

    optimizer = Adam(lr=0.0002, beta_1=0.5)
    discriminator.compile(loss='binary_crossentropy',
                          optimizer=optimizer,
                          metrics=None)

    return discriminator


# Creates the generator model. This model has an input of random noise and
# generates an image that will try mislead the discriminator.
def construct_generator():

    generator = Sequential()

    generator.add(Dense(units=4 * 4 * 512,
                        kernel_initializer='glorot_uniform',
                        input_shape=(1, 1, 100)))
    generator.add(Reshape(target_shape=(4, 4, 512)))
    generator.add(BatchNormalization(momentum=0.5))
    generator.add(Activation('relu'))

    generator.add(Conv2DTranspose(filters=256, kernel_size=(5, 5),
                                  strides=(2, 2), padding='same',
                                  data_format='channels_last',
                                  kernel_initializer='glorot_uniform'))
    generator.add(BatchNormalization(momentum=0.5))
    generator.add(Activation('relu'))

    generator.add(Conv2DTranspose(filters=128, kernel_size=(5, 5),
                                  strides=(2, 2), padding='same',
                                  data_format='channels_last',
                                  kernel_initializer='glorot_uniform'))
    generator.add(BatchNormalization(momentum=0.5))
    generator.add(Activation('relu'))

    generator.add(Conv2DTranspose(filters=64, kernel_size=(5, 5),
                                  strides=(2, 2), padding='same',
                                  data_format='channels_last',
                                  kernel_initializer='glorot_uniform'))
    generator.add(BatchNormalization(momentum=0.5))
    generator.add(Activation('relu'))

    generator.add(Conv2DTranspose(filters=3, kernel_size=(5, 5),
                                  strides=(2, 2), padding='same',
                                  data_format='channels_last',
                                  kernel_initializer='glorot_uniform'))
    generator.add(Activation('tanh'))

    optimizer = Adam(lr=0.00015, beta_1=0.5)
    generator.compile(loss='binary_crossentropy',
                      optimizer=optimizer,
                      metrics=None)
    return generator


# Displays a matrix of 64 x 64 of the generated images and saves them in as .png image
def save_images(generated_images, epoch, batch_number):

    plt.figure(figsize=(8, 8), num=2)
    gs1 = gridspec.GridSpec(8, 8)
    gs1.update(wspace=0, hspace=0)

    for i in range(64):
        ax1 = plt.subplot(gs1[i])
        ax1.set_aspect('equal')
        image = generated_images[i, :, :, :]
        image += 1
        image *= 127.5
        fig = plt.imshow(image.astype(np.uint8))
        plt.axis('off')
        fig.axes.get_xaxis().set_visible(False)
        fig.axes.get_yaxis().set_visible(False)

    plt.tight_layout()
    save_name = './gdrive/My Drive/App/generatedSamples_epoch' + str(
        epoch + 1) + '_batch' + str(batch_number + 1) +'.png'

    fig1 = plt.gcf()
    plt.show()
    plt.draw()
    plt.pause(0.0000000001)
    fig1.savefig(save_name, bbox_inches='tight', pad_inches=0)



# Main train function
def train_dcgan(batch_size, epochs, image_shape, dataset_path):
    # Build the adversarial model that consists in the generator output
    # connected to the discriminator
    generator = construct_generator()
    discriminator = construct_discriminator(image_shape)

    gan = Sequential()


    discriminator.trainable = False
    gan.add(generator)
    gan.add(discriminator)

    optimizer = Adam(lr=0.00015, beta_1=0.5)
    gan.compile(loss='binary_crossentropy', optimizer=optimizer,
                metrics=None)

    # Create a dataset Generator
    dataset_generator = load_dataset(dataset_path, batch_size, image_shape)

    # Use the number of images and the wanted batch size to calculate the number of batches
    number_of_batches = int(NO_IMAGES / batch_size)

    # Variables that will be used to plot the losses from the discriminator and
    # the adversarial models
    adversarial_loss = np.empty(shape=1)
    discriminator_loss = np.empty(shape=1)
    batches = np.empty(shape=1)
    accuracy = np.empty(shape=1)

    # Allow plot updates inside for loop
    plt.ion()

    current_batch = 0

    # Train the DCGAN for n epochs
    for epoch in range(epochs):

        print("Epoch " + str(epoch+1) + "/" + str(epochs) + " :")

        for batch_number in range(number_of_batches):

            start_time = time.time()

            # Get the current batch and normalize the images between -1 and 1

            real_images = dataset_generator.next()
            real_images = (real_images/ 127.5) - 1


            # The last batch is smaller than the other ones, so we need to
            # take that into account
            current_batch_size = real_images.shape[0]

            # Generate noise
            noise = np.random.normal(0, 1, size=(current_batch_size,) + (1, 1, 100))

            # Generate images
            generated_images = generator.predict(noise)

            # Add some noise to the labels that will be
            # fed to the discriminator
            real_y = (np.ones(current_batch_size) -
                      np.random.random_sample(current_batch_size) * 0.2)
            fake_y = np.random.random_sample(current_batch_size) * 0.2


            correct = 0
            acc = discriminator.predict(real_images)
            # print(acc)
            for i in range(len(acc)):
              if acc[i] >= 0.5:
                correct += 1
            acc = discriminator.predict(generated_images)
            for i in range(len(acc)):
              if acc[i] < 0.5:
                correct += 1
            print(correct)
            accuracy = np.append(accuracy, ((correct / (len(generated_images) + len(real_images)))))

            # Train the discriminator
            discriminator.trainable = True

            d_loss = discriminator.train_on_batch(real_images, real_y)
            d_loss += discriminator.train_on_batch(generated_images, fake_y)


            discriminator_loss = np.append(discriminator_loss, d_loss)


            # Train the generator
            discriminator.trainable = False

            noise = np.random.normal(0, 1,
                                     size=(current_batch_size * 2,) +
                                     (1, 1, 100))

            # Try to mislead the discriminator by giving the opposite labels
            fake_y = (np.ones(current_batch_size * 2) -
                      np.random.random_sample(current_batch_size * 2) * 0.2)

            g_loss = gan.train_on_batch(noise, fake_y)
            adversarial_loss = np.append(adversarial_loss, g_loss)
            batches = np.append(batches, current_batch)

            # Each 50 batches show and save images
            print("Current Batch: ", (current_batch + 1))
            if((batch_number + 1) % 50 == 0 and current_batch_size == batch_size):
               print("Saving Images...")
               save_images(generated_images, epoch, batch_number)

            if epoch + 1 == epochs and current_batch_size == batch_size:
               final_generated_images = generated_images

            time_elapsed = time.time() - start_time

            # Display and plot the results
            print("     Batch " + str(batch_number + 1) + "/" +
                  str(number_of_batches) +
                  " generator loss | discriminator loss : " +
                  str(g_loss) + " | " + str(d_loss) + ' - batch took ' +
                  str(time_elapsed) + ' s.')

            current_batch += 1

        # Save the model weights each 5 epochs
        if (epoch + 1) % 5 == 0:
            discriminator.trainable = True
            generator.save('./gdrive/My Drive/App/temp/generator_epoch' + str(epoch) + '.hdf5')
            discriminator.save('./gdrive/My Drive/App/temp/discriminator_epoch' +
                               str(epoch) + '.hdf5')

        plt.figure(1)
        plt.plot(batches, adversarial_loss, color='green',
                 label='Generator Loss')
        plt.plot(batches, discriminator_loss, color='blue',
                 label='Discriminator Loss')

        plt.title("DCGAN Train")
        plt.xlabel("Batch Iteration")
        plt.ylabel("Loss")
        if epoch == 0:
            plt.legend()

        fig1 = plt.gcf()
        plt.show()
        plt.draw()
        plt.pause(0.0000000001)
        fig1.savefig('./gdrive/My Drive/App/trainingLossPlot.png')

        plt.figure(1)
        plt.plot(batches, accuracy, color='blue',
                 label='Discriminator Accuracy')
        plt.title("DCGAN Train")
        plt.xlabel("Batch Iteration")
        plt.ylabel("Accuracy")
        if epoch == 0:
          plt.legend()
        fig1 = plt.gcf()
        plt.show()
        plt.draw()
        plt.pause(0.0000000001)
        fig1.savefig('./gdrive/My Drive/App/trainingAccuracyPlot.png')


def test_generator(image_shape):
  generator = construct_generator()
  discriminator = construct_discriminator(image_shape)
  generator.load_weights('./gdrive/My Drive/App/temp/generator_epoch179.hdf5')
  noise = np.random.normal(0, 1, size=(BATCH_SIZE,) + (1, 1, 100))
  generated_images = generator.predict(noise)
  save_images(generated_images, 1, 1)


if __name__ == "__main__":
    train_dcgan(BATCH_SIZE, EPOCHS,
                IMAGE_SHAPE, DATASET_PATH)
    #test_generator(IMAGE_SHAPE)
