import subprocess
import time
import requests
import webbrowser

DOCKER_COMPOSE_FILE = "/home/negocio/Escritorio/shoes-store/docker-compose.yml"
MYSQL_SERVICE_NAME = "mysql"  # nombre exacto del servicio en tu docker-compose
APP_URL = "http://localhost:8080/login"


def is_mysql_running():
    """Verifica si el contenedor mysql ya está corriendo"""
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


def start_docker():
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
            subprocess.run(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "up", "-d", "--build"] + services_to_start,
                check=True
            )
        else:
            print("⚠️ No hay servicios para levantar (todo está protegido o ya corriendo).")

    except subprocess.CalledProcessError as e:
        print("❌ Error en docker-compose:", e.stderr)
    except Exception as e:
        print(f"⚠️ Error inesperado: {e}")


def wait_for_app(url, timeout=120):
    """Espera hasta que la app esté disponible"""
    print(f"⏳ Esperando a que la aplicación esté lista en {url} ...")
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            response = requests.get(url, timeout=3)
            if response.status_code == 200:
                print("✅ La aplicación ya está disponible.")
                return True
        except requests.exceptions.RequestException:
            pass
        time.sleep(3)

    print("❌ La aplicación no respondió en el tiempo esperado.")
    return False


if __name__ == "__main__":
    print("🔼 Ejecutando script de arranque...")
    start_docker()

    if wait_for_app(APP_URL):
        print("🌐 Abriendo navegador en:", APP_URL)
        webbrowser.open(APP_URL)
    else:
        print("⚠️ No se pudo abrir el navegador porque la app no está lista.")



