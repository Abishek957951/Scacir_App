def realizar_detection(byte_array):
    # Se importan los paquetes necesario para que el progrma corra como debe
    import cv2
    import numpy as np
    import io
    import importlib.util
    from os.path import dirname, join


#Se estable el nivel minimo de confianza a la que el programa muestra las detecciones
    min_conf_threshold = 0.26
    pkg = importlib.util.find_spec('tflite_runtime')
    if pkg:
        from tflite_runtime.interpreter import Interpreter
    else:
        from tensorflow.lite.python.interpreter import Interpreter


    # Se carga el modela usando un nombre temporal
    interpreter = Interpreter(join(dirname(__file__), "detect.tflite"))
    interpreter.allocate_tensors()

    # Se crea la lsita que contiene todos los componentes que puede detectar el modelo
    labels = ["capacitor", "capacitorel", "inductor", "resistencia", "cable"]


    # Se obtienen los detalles del modelo que se usa
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    height = input_details[0]['shape'][1]
    width = input_details[0]['shape'][2]

    floating_model = (input_details[0]['dtype'] == np.float32)

    input_mean = 127.5
    input_std = 127.5

    # Check output layer name to determine if this model was created with TF2 or TF1,
    # because outputs are ordered differently for TF2 and TF1 models
    outname = output_details[0]['name']

    if ('StatefulPartitionedCall' in outname): # This is a TF2 model
        boxes_idx, classes_idx, scores_idx = 1, 3, 0
    else: # This is a TF1 model
        boxes_idx, classes_idx, scores_idx = 0, 1, 2

    # El byte_array se convierte a la imagen correspondiente, a la cual se le extraen los datos importantes
    image_stream = io.BytesIO(byte_array)
    image_stream.seek(0)
    file_bytes = np.asarray(bytearray(image_stream.read()), dtype=np.uint8)
    image = cv2.imdecode(file_bytes, cv2.IMREAD_COLOR)
    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    imH, imW, _ = image.shape 
    image_resized = cv2.resize(image_rgb, (width, height))
    input_data = np.expand_dims(image_resized, axis=0)

    # Se nomralizan los pixels de la imagen para llevar la imagen a una dimensión estandar.
    if floating_model:
        input_data = (np.float32(input_data) - input_mean) / input_std

    # Se realiza la detección por medio del Interpreter
    interpreter.set_tensor(input_details[0]['index'],input_data)
    interpreter.invoke()

    #Se obtienen los resultados de la detecció
    boxes = interpreter.get_tensor(output_details[boxes_idx]['index'])[0] # Bounding box coordinates of detected objects
    classes = interpreter.get_tensor(output_details[classes_idx]['index'])[0] # Class index of detected objects
    scores = interpreter.get_tensor(output_details[scores_idx]['index'])[0] # Confidence of detected objects

    detections = []
    data = {}
    # Por medio de un for se recorren todos las detecciones para agregargarlas a la lista de data, además de dibujar las cajas y etiquetas en la imagen
    for i in range(len(scores)):
        if ((scores[i] > min_conf_threshold) and (scores[i] <= 1.0)):

            # Se obtienen las coordenadas de las cajas. Las coordendas que da el modelo son basadas en una imagen 320x320
            #Por ende toca reescalar las detecciónes por los dimensiones de la imagen original
            ymin = int(max(1,(boxes[i][0] * imH)))
            xmin = int(max(1,(boxes[i][1] * imW)))
            ymax = int(min(imH,(boxes[i][2] * imH)))
            xmax = int(min(imW,(boxes[i][3] * imW)))
                
            cv2.rectangle(image, (xmin,ymin), (xmax,ymax), (10, 255, 0), 2)

            # Se dibuja la etiqueta
            object_name = labels[int(classes[i])] # Look up object name from "labels" array using class index
            label = '%d %s: %d%%' % (i, object_name, int(scores[i]*100)) # Example: 'person: 72%'
            labelSize, baseLine = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.7, 2) # Get font size
            label_ymin = max(ymin, labelSize[1] + 10) # Make sure not to draw label too close to top of window
            cv2.rectangle(image, (xmin, label_ymin-labelSize[1]-10), (xmin+labelSize[0], label_ymin+baseLine-10), (255, 255, 255), cv2.FILLED) # Draw white box to put label text in
            cv2.putText(image, label, (xmin, label_ymin-7), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 0), 2) # Draw label text

            detections.append([object_name, scores[i], xmin, ymin, xmax, ymax])
            #Lista que se va a usar para hacer las conexiones
            data[i]=[object_name,xmin, ymin, xmax, ymax, imH]    
        
    #Se guarda la imagen como un byte_array
    is_success, buffer = cv2.imencode(".jpg", image)
    if is_success:
        # Convert buffer to a byte array
        encoded_image = buffer.tobytes()
        return data, encoded_image
    else:
        return data, byte_array