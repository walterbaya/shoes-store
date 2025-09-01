import subprocess
import os
import signal

DOCKER_COMPOSE_FILE = "/home/negocio/Escritorio/shoes-store/docker-compose.yml"

def stop_docker():
    try:
        print("🛑 Apagando contenedores Docker...")
        subprocess.run(
            ["docker-compose", "-f", DOCKER_COMPOSE_FILE, "down"], 
            check=True
        )
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


