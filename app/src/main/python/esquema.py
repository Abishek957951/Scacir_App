def realizador_esquema(data,conexiones_componente,valores):
    import schemdraw as sd
    import schemdraw.elements as elm
    import random
    #Funcioes para hacer todo lo relacionado con el dibujo del circuito
    #Le asigna el tipo de componente que es
    def tipo(tipo):
        if(tipo == "resistencia"):
            return "elm.Resistor()"
        elif(tipo == "capacitor"):
            return "elm.Capacitor()"
        elif(tipo == "capacitorel"):
            return "elm.Capacitor()"
        elif(tipo == "inductor"):
            return "elm.Inductor()"
        elif(tipo == "cable"):
            return "elm.Line()"

    #Permite agregar las medidas de cada uno de los componentes
    def aux_etiqueta(lista_valores,i):
        if not lista_valores:
            return " "
        else:
            if(i > len(lista_valores)-1):
                return " "
            else:
                if(lista_valores[i] == "na"):
                    return " "
                elif(lista_valores[i]):
                    return lista_valores[i]

    #Le entrega a cada componente su etiqueta correspondiente
    def etiqueta(tipo,lista_valores,i):
        if(tipo == "resistencia"):
            return f".label('R-{aux_etiqueta(lista_valores,i)}')"
        elif(tipo == "capacitor"):
            return f".label('C-{aux_etiqueta(lista_valores,i)}')"
        elif(tipo == "capacitorel"):
            return f".label('CL-{aux_etiqueta(lista_valores,i)}')"
        elif(tipo == "inductor"):
            return f".label('L-{aux_etiqueta(lista_valores,i)}')"
        elif(tipo == "cable"):
            return f".label('W-{aux_etiqueta(lista_valores,i)}')"

    #Función especial para dibujar componentes en paralelo
    def paralelos(componente_prin, paralelos,posicion,lista_Valores):
        pos_lin_aux_1 = None
        pos_lin_aux_2 = None
        paralelos.insert(0,componente_prin)
        c_par = len(paralelos)
        if posicion == ".right()" or posicion == ".left()":
            pos_lin_aux_1 = ".up()"
            pos_lin_aux_2 = ".down()"
        else:
            pos_lin_aux_1 = ".right()"
            pos_lin_aux_2 = ".left()"

        if c_par == 2:
            code_text = ["d.push()",
                         f"d += elm.Line(){pos_lin_aux_1}.length(0.5)",
                         f"d += {tipo(data[paralelos[0]][0])}{posicion}{etiqueta(data[paralelos[0]][0],lista_Valores,paralelos[0])}",
                         f"d += elm.Line(){pos_lin_aux_2}.length(0.5)",
                         "d.pop()",
                         f"d += elm.Line(){pos_lin_aux_2}.length(0.5)",
                         f"d += {tipo(data[paralelos[1]][0])}{posicion}{etiqueta(data[paralelos[1]][0],lista_Valores,paralelos[1])}",
                         f"d += elm.Line(){pos_lin_aux_1}.length(0.5)",
                         f"d += elm.Line(){posicion}.length(0.2)"]

        elif c_par == 3:
            code_text = ["d.push()",
                         f"d += {tipo(data[paralelos[0]][0])}{posicion}{etiqueta(data[paralelos[0]][0],lista_Valores,paralelos[0])}",
                         "d.pop()",
                         "d.push()",
                         f"d += elm.Line(){pos_lin_aux_1}.length(1)",
                         f"d += {tipo(data[paralelos[1]][0])}{posicion}{etiqueta(data[paralelos[1]][0],lista_Valores,paralelos[1])}",
                         f"d += elm.Line(){pos_lin_aux_2}.length(1)",
                         "d.pop()",
                         f"d += elm.Line(){pos_lin_aux_2}.length(1)",
                         f"d += {tipo(data[paralelos[2]][0])}{posicion}{etiqueta(data[paralelos[2]][0],lista_Valores,paralelos[2])}",
                         f"d += elm.Line(){pos_lin_aux_1}.length(1)",
                         f"d += elm.Line(){posicion}.length(0.2)"]

        elif c_par == 4:
            code_text = ["d.push()",
                         f"d += (AUX1 := elm.Line(){pos_lin_aux_1}.length(0.30))",
                         "d += elm.Line().length(0.55)",
                         f"d += {tipo(data[paralelos[0]][0])}{posicion}{etiqueta(data[paralelos[0]][0],lista_Valores,paralelos[0])}",
                         f"d += (AUX2 := elm.Line(){pos_lin_aux_2}.length(0.55))",
                         "d += elm.Line().length(0.30)",
                         "d.pop()",
                         f"d += (AUX3 :=elm.Line(){pos_lin_aux_2}.length(0.30))",
                         "d += elm.Line().length(0.55)",
                         f"d += {tipo(data[paralelos[1]][0])}{posicion}{etiqueta(data[paralelos[1]][0],lista_Valores,paralelos[1])}",
                         f"d += (AUX4 := elm.Line(){pos_lin_aux_1}.length(0.55))",
                         "d += elm.Line().length(0.30)",
                         f"d += elm.Line(){posicion}.length(1)",
                         f"d += {tipo(data[paralelos[2]][0])}{posicion}{etiqueta(data[paralelos[2]][0],lista_Valores,paralelos[2])}.endpoints(AUX1.end,AUX2.end)",
                         f"d += {tipo(data[paralelos[3]][0])}{posicion}{etiqueta(data[paralelos[3]][0],lista_Valores,paralelos[3])}.endpoints(AUX3.end,AUX4.end)"]

        elif c_par == 5:
            code_text = ["d.push()",
                         f"d += {tipo(data[paralelos[0]][0])}{posicion}{etiqueta(data[paralelos[0]][0],lista_Valores,paralelos[0])}",
                         "d.pop()",
                         "d.push()",
                         f"d += (AUX1 := elm.Line(){pos_lin_aux_1}.length(0.6))",
                         "d += elm.Line().length(0.6)",
                         f"d += {tipo(data[paralelos[1]][0])}{posicion}{etiqueta(data[paralelos[1]][0],lista_Valores,paralelos[1])}",
                         f"d += (AUX2 := elm.Line(){pos_lin_aux_2}.length(0.6))",
                         "d += elm.Line().length(0.6)",
                         "d.pop()",
                         f"d += (AUX3 :=elm.Line(){pos_lin_aux_2}.length(0.6))",
                         "d += elm.Line().length(0.6)",
                         f"d += {tipo(data[paralelos[2]][0])}{posicion}{etiqueta(data[paralelos[2]][0],lista_Valores,paralelos[2])}",
                         f"d += (AUX4 := elm.Line(){pos_lin_aux_1}.length(0.6))",
                         "d += elm.Line().length(0.6)",
                         f"d += elm.Line(){posicion}.length(1)",
                         f"d += {tipo(data[paralelos[3]][0])}{posicion}{etiqueta(data[paralelos[3]][0],lista_Valores,paralelos[3])}.endpoints(AUX1.end,AUX2.end)",
                         f"d += {tipo(data[paralelos[4]][0])}{posicion}{etiqueta(data[paralelos[4]][0],lista_Valores,paralelos[4])}.endpoints(AUX3.end,AUX4.end)]"]

        return code_text

    #Función que le dice al componente que dirección debe tomar, para formar un circuito cuadricular.
    def posicionador(lista):
        cantidad = len(lista)
        orden = {}
        if cantidad == 4:
            orden = {1:".right()", 2:".down()", 3:".left()", 4:".up()"}
        if cantidad == 8:
            orden = {1:".right()", 2:".right()", 3:".down()", 4:".down()", 5:".left()", 6:".left()", 7:".up()", 8:".up()"}
        if cantidad == 12:
            orden = {1:".right()", 2:".right()", 3:".right()", 4:".down()", 5:".down()", 6:".down()", 7:".left()", 8:".left()"
                , 9:".left()", 10:".up()", 11:".up()", 12:".up()"}
        return orden

    #Agrega lineas faltantes en el caso de que no se pueda fomrar un cuadrado con los omponentes dado
    def agregador_lineas(lista):
        num_lineas = 0
        if len(lista) < 4:
            num_lineas = 4 - len(lista)
        elif 4 < len(lista) < 8:
            num_lineas = 8 - len(lista)
        elif 8 < len(lista) :
            num_lineas = 12 - len(lista)
        else:
            num_lineas = 0
        for i in range(num_lineas):
            lista.append(101)
        return lista

    #Se obtiene los valores de la lista de valores y se escoge el primer componente según el que se encuentra mas a la derecha
    lista_valores = valores.split()
    orden_componentes = sorted(data, key=lambda key: data[key][1])
    pasador_componente = orden_componentes[0]


    #Lista que muestra como se van a llevar a cabo las conexiones
    lista_componentes_ya_estan = []
    componente1 = None
    while pasador_componente not in lista_componentes_ya_estan:
        #Variable iniciales con las que se empieza cada vez el ciclo
        componente1  = pasador_componente
        lista_componentes_ya_estan.append(componente1)
        conexiones_der = []
        conexiones_iz = []


        for valor in conexiones_componente[componente1]:
            if valor == 'derecha':
                conexiones_der.append(conexiones_componente[componente1][conexiones_componente[componente1].index(valor)-1])
            if valor == 'izquierda':
                conexiones_iz.append(conexiones_componente[componente1][conexiones_componente[componente1].index(valor)-1])

        if len(conexiones_der) != 0:
            componente_der = conexiones_der[0]
        if len(conexiones_iz) != 0:
            componente_iz = conexiones_iz[0]

        if componente_der in lista_componentes_ya_estan:
            pasador_componente = componente_iz
        else:
            pasador_componente = componente_der



    lista_circuito_final = agregador_lineas(lista_componentes_ya_estan)


    #Código que por medio de schemdraw y exec es capaz de dibujar el circuitoy lo retonr aomo un bye_array
    with sd.Drawing() as d:
        pasador = 0
        posicion = posicionador(lista_circuito_final)
        for componente in lista_circuito_final:
            pasador+=1
            if componente != 101:
                if "paralelo" in conexiones_componente[componente]:
                    conexiones_par = []
                    for valor in conexiones_componente[componente]:
                        if valor == "paralelo":
                            conexiones_par.append(conexiones_componente[componente][conexiones_componente[componente].index(valor)-1])
                    code_text= paralelos(componente,conexiones_par,posicion[pasador],lista_valores)
                    for codigo in code_text:
                        exec(codigo)
                else:
                    exec(f"d += {tipo(data[componente][0])}{posicion[pasador]}{etiqueta(data[componente][0],lista_valores,componente)}")
            else:
                exec(f"d += elm.Line(){posicion[pasador]}")

        img_bytes_svg = d.get_imagedata('svg')

    return conexiones_der, lista_componentes_ya_estan, img_bytes_svg
                                 


    
