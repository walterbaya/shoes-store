import subprocess
import time
import requests
import webbrowser
import threading
from flask import Flask, request, jsonify

# --- CONFIGURACIÓN ---
DOCKER_COMPOSE_FILE = "/home/negocio/Escritorio/shoes-store/docker-compose.yml"
MYSQL_SERVICE_NAME = "mysql"
APP_URL = "http://localhost:8080/login"
CHECK_INTERVAL = 2  # segundos entre cada chequeo de disponibilidad
FLASK_PORT = 4000

# --- FLASK ---
app = Flask(__name__)

def run_deploy():
    """Ejecuta el deploy automáticamente usando docker-compose."""
    try:
        print("🚀 Ejecutando deploy...")
        subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "pull"],
            check=True
        )
        subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "up", "-d", "--build"],
            check=True
        )
        print("✅ Deploy completado.")
    except subprocess.CalledProcessError as e:
        print(f"❌ Error en deploy: {e}")

@app.route("/", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)
    threading.Thread(target=run_deploy).start()
    return jsonify({"status": "accepted"}), 202

# --- FUNCIONES DE DOCKER Y APP ---
def is_mysql_running():
    """Verifica si MySQL ya está corriendo en Docker."""
    try:
        result = subprocess.run(
            ["docker", "ps", "--filter", f"name={MYSQL_SERVICE_NAME}", "--format", "{{.Names}}"],
            capture_output=True, text=True, check=True
        )
        running = result.stdout.strip().splitlines()
        return any(MYSQL_SERVICE_NAME in name for name in running)
    except Exception as e:
        print(f"⚠️ Error al verificar MySQL: {e}")
        return False

def wait_for_app(url, timeout=120):
    """Espera hasta que la aplicación responda y abre el navegador."""
    print(f"⏳ Esperando a que la aplicación esté disponible en {url}...")
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            response = requests.get(url)
            if response.status_code == 200:
                print(f"\n✅ Aplicación lista en {url}")
                webbrowser.open(url)
                return True
        except requests.exceptions.ConnectionError:
            pass
        time.sleep(CHECK_INTERVAL)
    print("❌ Tiempo de espera agotado. La app no respondió.")
    return False

def start_docker():
    """Levanta los servicios docker mostrando logs en tiempo real."""
    try:
        # Obtener lista de servicios
        result = subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "config", "--services"],
            capture_output=True, text=True, check=True
        )
        services = result.stdout.strip().splitlines()

        # Si MySQL ya corre, no lo levantamos
        services_to_start = services
        if is_mysql_running():
            print("✅ MySQL ya está corriendo, no se volverá a levantar.")
            services_to_start = [s for s in services if s != MYSQL_SERVICE_NAME]

        if services_to_start:
            # Levantar contenedores en background
            print(f"🚀 Levantando servicios: {', '.join(services_to_start)}")
            subprocess.run(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "up", "-d", "--build"] + services_to_start,
                check=True
            )
            print("✅ Contenedores levantados en background")

            # Mostrar logs en tiempo real
            subprocess.run(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "logs", "-f"] + services_to_start
            )
        else:
            print("⚠️ No hay servicios para levantar (todo ya está corriendo).")

    except subprocess.CalledProcessError as e:
        print("❌ Error en docker-compose:", e.stderr)
    except Exception as e:
        print(f"⚠️ Error inesperado: {e}")

# --- EJECUCIÓN PRINCIPAL ---
if __name__ == "__main__":
    print("🔼 Iniciando sistema unificado...")

    # Hilo para levantar Docker
    docker_thread = threading.Thread(target=start_docker)
    docker_thread.start()

    # Hilo para esperar que la app esté lista y abrir navegador
    app_thread = threading.Thread(target=wait_for_app, args=(APP_URL,))
    app_thread.start()

    # Hilo para Flask (webhook deploy)
    flask_thread = threading.Thread(target=lambda: app.run(host="0.0.0.0", port=FLASK_PORT))
    flask_thread.start()

    # Esperamos a los hilos principales
    docker_thread.join()
    app_thread.join()
    flask_thread.join()











