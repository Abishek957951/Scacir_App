def realizador_conexiones_componentes(data):
    #Funcion que se ecarga de medir las distancias verticales entre los demas componentes a el componente fijo
    def categorizador_vertical(imgH,y_ab, y_ar, y_min, y_max):
        distancia = 0
        posicion_cp = ""
        posicion_cs= ""
        
        #La posicion de los componenetes con respecto a la franja del medio
        
        #La posición del componente principal con respecto a la franja de la mitad
        if(y_ab >= imgH and  y_ar >= imgH):
            posicion_cp = "arriba"
        elif(y_ab <= imgH and y_ar <= imgH):
            posicion_cp = "abajo"
        else:
            posicion_cp = "medio" 
        #La posición del componente secundario con respecto a la franja de la mitad
        if(y_min >= imgH and  y_max >= imgH):
            posicion_cs = "arriba"
        elif(y_min <= imgH and y_max <= imgH):
            posicion_cs = "abajo"
        else:
            posicion_cs = "medio"
            
        #La distancia de los componentes ya sabiendo su posición con respecto a la franja de la mitad
        
        #Si ambos componenentes se encuentran por en el mismo lado de la franja
        if((posicion_cp != "medio") and (posicion_cs != "medio") and (posicion_cp == posicion_cs)):
            distancia = 0
        #Si ambos componenentes se encuentran en diferentes lados de la frnaja
        elif((posicion_cp != "medio") and (posicion_cs != "medio") and (posicion_cp != posicion_cs) ):
            distancia = 1000  
        #Si uno o ambos componenentes se encuentran atravesando la franja 
        elif((posicion_cp == "medio") or (posicion_cs == "medio")):
            #Si el componentes se encuentran cercanamente borde inferior-superior
            if((y_min <= y_ab) and (y_max <= y_ab)):
                distancia = y_ab - y_max
            #Si el componentes se encuentran cercanamente borde superior-inferior
            elif((y_min >= y_ar) and (y_max >= y_ar)):
                distancia = y_min - y_ar
            #Si los componentes se encuentran atravesando el uno del otro o uno "contiene a otro"
            elif(((y_min <= y_ar) and (y_max >= y_ar)) or ((y_ab <= y_min) and 
                    (y_max <= y_ar)) or ((y_min <= y_ab) and (y_max >= y_ar)) or ((y_min <= y_ab) and (y_max <= y_ar))):
                d_centro_ar = abs(y_ar - y_min)
                d_centro_interno_ar = abs(y_ar - y_max)
                d_centro_interno_ab = abs(y_min - y_ab)
                d_centro_externo_ar = abs(y_ar - y_min)
                d_centro_externo_ab = abs(y_max - y_ab)
                d_centro_ab = abs(y_max - y_ab)
                distancias = [d_centro_ar, d_centro_interno_ar, d_centro_interno_ab,  d_centro_externo_ar, d_centro_externo_ab, d_centro_ab]
                distancias.sort()
                distancia = distancias[0]        
        return distancia
            
    #Funcion que filtra todos los resultados del categorizador vertical y los prepara para la siguiente fase       
    def etapa_clasificacion_y(index,distancia):
        if(distancia <= 260):
            return index
        
    #Funciones que se van a implementar el la parte de detección de qué componente es cerca a qué 
    def categorizador_horizontal(x_iz, x_der, x_min, x_max):
        distancia = 0
        ubicacion_x = ""
        #Si el componente se encuentra en la izquierda
        if((x_min <= x_iz) and (x_max <= x_iz)):
            distancia = x_iz - x_max
            ubicacion_x = "izquierda"
        #Si el componente se encuentra en la derecha
        elif((x_min >= x_der) and (x_max >= x_der)):
            distancia = x_min - x_der
            ubicacion_x = "derecha"
        #Si el componente se encuentra mas o menos en el centro
        elif(((x_min <= x_der) and (x_max >= x_der)) or ((x_iz <= x_min) and 
                (x_max <= x_der)) or ((x_min <= x_iz) and (x_max >= x_der)) or ((x_min <= x_iz) and (x_max <= x_der))):
            d_centro_der = abs(x_der - x_min)
            d_centro_interno_der = abs(x_der - x_max)
            d_centro_interno_iz = abs(x_min - x_iz)
            d_centro_externo_der = abs(x_der - x_min)
            d_centro_externo_iz = abs(x_max - x_iz)
            d_centro_iz = abs(x_max - x_iz)
            distancias = [d_centro_der, d_centro_interno_der, d_centro_interno_iz,  d_centro_externo_der, d_centro_externo_iz, d_centro_iz]
            distancias.sort()
            distancia = distancias[0]
            if((distancia == d_centro_der) or (distancia == d_centro_interno_der) or (distancia == d_centro_externo_der)):
                ubicacion_x = "derecha"
            elif((distancia == d_centro_iz) or (distancia == d_centro_interno_iz) or (distancia == d_centro_externo_iz)):
                ubicacion_x = "izquierda"
        #En el caso de que ambos son paralalelos
        if(abs(x_min - x_iz) <= 50 and abs(x_max - x_der) <= 50):
            distancia = 50
            ubicacion_x = "paralelo"
        return distancia, ubicacion_x
        
    #Funcion que filtra todos los resultados del categorizador horizontal y los prepara para la siguiente fase de nodos       
    def etapa_clasificacion_x(index, distancia, ubicacion_x):
        if(distancia <=165):
            return index, ubicacion_x
        else:
            return 101 ,"nada"
    
    conexiones_componente = {}
    for key, value in data.items():
            
        x_der = value[3]
        x_iz = value[1]
        y_ar = value[4]
        y_ab = value[2]
        imgH = value[5] / 2
        
        fase1=[]      
        for llave, valor in data.items():
            y_min = valor[2]
            y_max = valor[4]
            if((llave != key)):
                fase1.append(etapa_clasificacion_y(llave,categorizador_vertical(imgH, y_ab, y_ar, y_min, y_max)))
                    
        conexiones_componente[key] = [] 
        for schlussel, wert in data.items():  
            x_min = wert[1]
            x_max = wert[3]   
            y_min_extra = wert[2]
            y_max_extra = wert[4]  
            if(schlussel in fase1):
                resultado1_ch = 0
                resultado2_ch = ""
                resultado1_et = 0
                resultado2_et = "" 
                resultado1_ch, resultado2_ch = categorizador_horizontal(x_iz, x_der, x_min, x_max)
                resultado1_et, resultado2_et = etapa_clasificacion_x(schlussel, resultado1_ch, resultado2_ch)
                #Comprobante adiconal para los componentes principales con orientación vertical
                if(((x_der - x_iz) < (y_ar - y_ab)) and (y_ab < imgH < y_ar)):
                    if((resultado1_et != 101) and (abs(y_min_extra - y_ar) < 200)):
                        resultado2_et = "izquierda"
                    elif((resultado1_et != 101) and (abs(y_ab - y_max_extra) < 200)):
                        resultado2_et = "derecha"
                        
                conexiones_componente[key].append(resultado1_et)
                conexiones_componente[key].append(resultado2_et)
                
    return conexiones_componente          

        
    
          
    








    


    
    
    
