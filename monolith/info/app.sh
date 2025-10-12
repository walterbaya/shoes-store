#!/bin/bash

APP_PORT=4000
LOGIN_URL="http://localhost:8080/login"
PYTHON_SCRIPT="/home/negocio/Escritorio/shoes-store/script.py"
PYTHON_SCRIPT_CLOSE="/home/negocio/Escritorio/shoes-store/script-close.py"

# --- FUNCION DE LIMPIEZA ---
cleanup() {
    echo ""
    echo "🛑 Cerrando todo..."

    # Apagar hookdeck
    if [ -n "$HOOKDECK_PID" ] && kill -0 $HOOKDECK_PID 2>/dev/null; then
        echo "  - Apagando Hookdeck (PID: $HOOKDECK_PID)"
        kill -9 $HOOKDECK_PID
    fi

    # Apagar flask
    if [ -n "$PYTHON_PID" ] && kill -0 $PYTHON_PID 2>/dev/null; then
        echo "  - Apagando Flask (PID: $PYTHON_PID)"
        kill -9 $PYTHON_PID
    fi

    # Apagar docker
    echo "  - Apagando Docker containers..."
    docker-compose down

    # Ejecutar script de cierre
    echo "  - Ejecutando script-close.py..."
    python3 $PYTHON_SCRIPT_CLOSE

    echo "✅ Todo apagado."
    exit 0
}

# Capturar señales (Ctrl+C o cerrar terminal)
trap cleanup SIGINT SIGTERM EXIT

# --- ARRANCAR HOOKDECK ---
echo "▶️ Iniciando Hookdeck..."
hookdeck listen $APP_PORT Source &
HOOKDECK_PID=$!

# --- ARRANCAR FLASK ---
echo "▶️ Iniciando servidor Flask..."
python3 $PYTHON_SCRIPT &
PYTHON_PID=$!

# --- ESPERAR A QUE FLASK ARRANQUE ---
echo "⏳ Esperando a que Flask inicie en el puerto $APP_PORT..."
while ! nc -z localhost $APP_PORT; do
    sleep 1
done

# --- ABRIR LOGIN EN NAVEGADOR ---
echo "🌐 Flask iniciado. Abriendo navegador en $LOGIN_URL ..."
xdg-open "$LOGIN_URL" &

echo "✅ Todo en marcha. Presiona Ctrl+C o cierra la terminal para apagar."

# Mantener script en foreground mostrando salida del python
wait $PYTHON_PID


