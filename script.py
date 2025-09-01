import subprocess
import time
import requests
import webbrowser
import threading

DOCKER_COMPOSE_FILE = "/home/negocio/Escritorio/shoes-store/docker-compose.yml"
MYSQL_SERVICE_NAME = "mysql"
APP_URL = "http://localhost:8080/login"
CHECK_INTERVAL = 2  # segundos entre cada chequeo de disponibilidad

def is_mysql_running():
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

def wait_for_app(url):
    """Espera hasta que la aplicación responda y abre el navegador."""
    print(f"⏳ Esperando a que la aplicación esté disponible en {url}...")
    while True:
        try:
            response = requests.get(url)
            if response.status_code == 200:
                print(f"\n✅ Aplicación lista en {url}")
                webbrowser.open(url)
                break
        except requests.exceptions.ConnectionError:
            pass
        time.sleep(CHECK_INTERVAL)

def start_docker():
    """Levanta los servicios docker mostrando logs en tiempo real."""
    try:
        # Obtener lista de servicios definidos en docker-compose
        result = subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "config", "--services"],
            capture_output=True, text=True, check=True
        )
        services = result.stdout.strip().splitlines()

        # Si MySQL ya corre, lo sacamos de la lista de arranque
        if is_mysql_running():
            print("✅ MySQL ya está corriendo, no se volverá a levantar.")
            services_to_start = [s for s in services if s != MYSQL_SERVICE_NAME]
        else:
            services_to_start = services

        if services_to_start:
            print(f"🚀 Levantando servicios: {', '.join(services_to_start)}")
            process = subprocess.Popen(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "up", "--build"] + services_to_start,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True
            )

            # Mostrar logs en tiempo real
            for line in process.stdout:
                print(line, end="")

            process.wait()
            print(f"✅ Servicios levantados con código de salida {process.returncode}")
        else:
            print("⚠️ No hay servicios para levantar (todo está protegido o ya corriendo).")

    except subprocess.CalledProcessError as e:
        print("❌ Error en docker-compose:", e.stderr)
    except Exception as e:
        print(f"⚠️ Error inesperado: {e}")

if __name__ == "__main__":
    print("🔼 Ejecutando script de arranque...")

    # Hilo para levantar docker y mostrar logs
    docker_thread = threading.Thread(target=start_docker)
    docker_thread.start()

    # Hilo para esperar que la app esté lista y abrir navegador
    app_thread = threading.Thread(target=wait_for_app, args=(APP_URL,))
    app_thread.start()

    # Esperamos que ambos hilos terminen
    docker_thread.join()
    app_thread.join()

    print("✅ Arranque completo y navegador abierto.")









