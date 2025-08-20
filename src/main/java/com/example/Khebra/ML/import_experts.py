import csv
import requests
from tqdm import tqdm  # Pour la barre de progression

# Configuration
CSV_FILE = 'experts_complet.csv'
API_URL = 'http://localhost:8088/api/v1/auth/register'
HEADERS = {'Content-Type': 'application/json'}

def import_experts():
    # Ouvrir le fichier CSV
    with open(CSV_FILE, mode='r', encoding='utf-8') as csvfile:
        csv_reader = csv.DictReader(csvfile)
        experts = list(csv_reader)  # Convertir en liste pour compter les lignes

        # Initialiser la barre de progression
        for expert in tqdm(experts, desc="Importation des experts"):
            # Préparer le payload JSON
            payload = {
                "firstname": expert['firstname'],
                "lastname": expert['lastname'],
                "email": expert['email'],
                "password": expert['password'],
                "numTel": expert['numTel'],
                "cin": expert['cin'],
                "address": expert['address'],
                "userType": expert['userType'],
                "point": expert['point'],
                "expertise": expert['expertise'],
                "DomaineId": int(expert['DomaineId']),
                "biographie": expert['biographie'],
            }

            try:
                # Envoyer la requête POST
                response = requests.post(API_URL, json=payload, headers=HEADERS)

                # Vérifier la réponse
                if response.status_code != 200:
                    print(f"\nErreur pour {expert['email']}: {response.status_code} - {response.text}")

            except requests.exceptions.RequestException as e:
                print(f"\nErreur de connexion pour {expert['email']}: {str(e)}")

if __name__ == '__main__':
    print("Début de l'importation des experts...")
    import_experts()
    print("Importation terminée.")