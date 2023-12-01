from detecciones import realizar_detection
from conexiones import realizador_conexiones_componentes
from esquema import realizador_esquema

data = {}

def fase1(byte_array):
    global data
    datos, imagen = realizar_detection(byte_array)
    data = datos
    return imagen

def fase2(valores):
    global data
    primero,lista_componentes_ya_estan,imagen_esquema = realizador_esquema(data,realizador_conexiones_componentes(data), valores)
    return data, realizador_conexiones_componentes(data), imagen_esquema, lista_componentes_ya_estan,primero
    



                                 


    
    
    
