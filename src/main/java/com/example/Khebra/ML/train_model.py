import pickle
from datetime import datetime
from sqlalchemy import create_engine, text
from sentence_transformers import SentenceTransformer
from sklearn.neighbors import NearestNeighbors

# -----------------------
# 1. Connexion DB
# -----------------------
DATABASE_URL = "postgresql://postgres:root@localhost:5432/khebra"
engine = create_engine(DATABASE_URL)

# -----------------------
# 2. Récupération des données
# -----------------------
query = text("""
    SELECT
        u.firstname,
        u.lastname,
        e.expertise,
        e.biographie,
        d.name AS domaine,
        u.email
    FROM expert e
    JOIN _user u ON e.id = u.id
    JOIN domaine d ON e.domaine_id = d.id
    WHERE u.is_active = true
""")

with engine.connect() as conn:
    result = conn.execute(query)
    columns = result.keys()
    experts_data = [dict(zip(columns, row)) for row in result.fetchall()]

# -----------------------
# 3. Préparer les textes pour l'encodage
# -----------------------
# Même logique que l'ancienne version : expertise + domaine + biographie
texts = [
    f"{exp['expertise'] or ''} {exp['domaine'] or ''} {exp['biographie'] or ''}"
    for exp in experts_data
]

# -----------------------
# 4. Charger le modèle SentenceTransformer
# -----------------------
sentence_model = SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')
embeddings = sentence_model.encode(texts)

# -----------------------
# 5. Créer et entraîner KNN
# -----------------------
knn_model = NearestNeighbors(n_neighbors=5, metric='cosine')
knn_model.fit(embeddings)

# -----------------------
# 6. Sauvegarde dans experts.pkl
# -----------------------
with open('experts.pkl', 'wb') as f:
    pickle.dump({
        'knn_model': knn_model,
        'sentence_model': sentence_model,
        'experts_data': experts_data,  # liste de dict, même format que CSV
        'training_date': datetime.now().isoformat()
    }, f)

print("Modèle réentraîné à partir de PostgreSQL et sauvegardé dans experts.pkl")
