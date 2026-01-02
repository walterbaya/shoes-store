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
    """Ejecuta el deploy automáticamente usando docker-compose y recarga la app."""
    try:
        print("🚀 Ejecutando deploy...")
        # Traer nuevas imágenes
        subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "pull"],
            check=True
        )
        # Levantar o actualizar contenedores
        subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "up", "-d", "--build"],
            check=True
        )
        print("✅ Deploy completado. Recargando navegador...")

        # Abrir o recargar la app en el navegador
        webbrowser.open(APP_URL)
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
    """Levanta los servicios docker en background."""
    try:
        result = subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "config", "--services"],
            capture_output=True, text=True, check=True
        )
        services = result.stdout.strip().splitlines()

        # No levantar MySQL si ya corre
        services_to_start = [s for s in services if s != MYSQL_SERVICE_NAME] if is_mysql_running() else services

        if services_to_start:
            print(f"🚀 Levantando servicios: {', '.join(services_to_start)}")
            subprocess.run(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "up", "-d", "--build"] + services_to_start,
                check=True
            )
            print("✅ Contenedores levantados en background")

            # Lanzar hilo para logs en tiempo real (opcional)
            def follow_logs():
                subprocess.run(
                    ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "logs", "-f"] + services_to_start
                )
            threading.Thread(target=follow_logs, daemon=True).start()

        else:
            print("⚠️ No hay servicios para levantar (todo ya está corriendo).")
    except Exception as e:
        print(f"⚠️ Error al levantar Docker: {e}")

# --- EJECUCIÓN PRINCIPAL ---
if __name__ == "__main__":
    print("🔼 Iniciando sistema unificado...")

    # 1️⃣ Levantar Docker (bloquea hasta que termine)
    start_docker()

    # 2️⃣ Esperar que la app responda y abrir navegador
    wait_for_app(APP_URL)

    # 3️⃣ Iniciar Flask en un hilo separado para recibir webhooks
    flask_thread = threading.Thread(target=lambda: app.run(host="0.0.0.0", port=FLASK_PORT))
    flask_thread.start()
    flask_thread.join()




