import subprocess
import os
import signal

DOCKER_COMPOSE_FILE = "/home/negocio/Escritorio/shoes-store/docker-compose.yml"
MYSQL_SERVICE_NAME = "mysql"  # nombre del servicio en docker-compose.yml

def stop_docker():
    try:
        print("🛑 Apagando contenedores Docker (excepto MySQL)...")

        # Obtener lista de servicios definidos en docker-compose
        result = subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "config", "--services"],
            capture_output=True, text=True, check=True
        )
        services = result.stdout.strip().splitlines()

        # Filtrar servicios excepto mysql
        services_to_stop = [s for s in services if s != MYSQL_SERVICE_NAME]

        if services_to_stop:
            subprocess.run(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "stop"] + services_to_stop,
                check=True
            )
            subprocess.run(
                ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "rm", "-f"] + services_to_stop,
                check=True
            )
        else:
            print("⚠️ No hay servicios para detener (solo existe MySQL en el compose).")

    except Exception as e:
        print(f"⚠️ Error al apagar Docker: {e}")

def free_ports(ports):
    """Libera los puertos matando procesos que los usen"""
    for port in ports:
        try:
            result = subprocess.run(
                f"lsof -ti:{port}", shell=True, capture_output=True, text=True
            )
            pids = result.stdout.strip().splitlines()
            for pid in pids:
                print(f"🛑 Matando proceso PID {pid} en puerto {port}")
                os.kill(int(pid), signal.SIGKILL)
        except Exception as e:
            print(f"⚠️ No se pudo liberar el puerto {port}: {e}")

if __name__ == "__main__":
    print("🔻 Ejecutando script de apagado...")

    # Apagar docker usando ruta absoluta del docker-compose.yml
    stop_docker()

    # Opcional: liberar puertos que uses (ej. Flask 4000 y app 8080)
    free_ports([4000, 8080])

    print("✅ Apagado completo.")



