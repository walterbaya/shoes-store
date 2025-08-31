from flask import Flask, request
import os

app = Flask(__name__)

@app.route("/deploy", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)
    # Traer última imagen y levantar contenedores
    os.system("docker-compose pull && docker-compose up -d --build")
    return {"status": "ok"}

if __name__ == "__main__":
    app.run(port=4000)
