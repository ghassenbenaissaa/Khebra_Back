from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from schemas import ExpertOut, RecommendationRequest, NoExpertResponse
import numpy as np
from typing import List
import logging
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker
import pickle
import os

# Configuration du logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# Configuration CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Configuration de la base de données
DATABASE_URL = "postgresql://postgres:root@localhost:5432/khebra"
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Chargement du modèle NLP/KNN
def load_model():
    model_path = "experts.pkl"
    if not os.path.exists(model_path):
        raise RuntimeError("Modèle non trouvé")

    with open(model_path, "rb") as f:
        return pickle.load(f)

try:
    model = load_model()
    sentence_model = model['sentence_model']
    knn_model = model['knn_model']
except Exception as e:
    logger.error(f"Erreur de chargement du modèle: {str(e)}")
    sentence_model = None
    knn_model = None

# Dépendance pour la base de données
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post("/recommend", response_model=List[ExpertOut], responses={
    404: {"model": NoExpertResponse}
})
async def recommend(request: RecommendationRequest, db=Depends(get_db)):
    """
    Recommande max 3 experts avec le score de similarité le plus élevé
    """
    if not sentence_model or not knn_model:
        raise HTTPException(status_code=503, detail="Service de recommandation indisponible")

    try:
        # 1. Encodage du problème
        problem_embedding = sentence_model.encode([request.problem])

        # 2. Récupération des experts actifs avec domaine
        query = text("""
            SELECT
                u.id,
                u.firstname,
                u.lastname,
                u.email,
                e.expertise,
                e.biographie,
                d.name AS domaine
            FROM expert e
            JOIN _user u ON e.id = u.id
            JOIN domaine d ON e.domaine_id = d.id
            WHERE u.is_active = true
        """)
        experts = db.execute(query).fetchall()

        if not experts:
            raise HTTPException(
                status_code=404,
                detail={
                    "message": "Aucun expert disponible",
                    "suggestion": "Réessayez plus tard"
                }
            )

        # 3. Texte pour l'encodage
        expert_texts = [
            f"{exp.expertise or ''} {exp.domaine or ''} {exp.biographie or ''}"
            for exp in experts
        ]

        if not any(expert_texts):
            raise HTTPException(
                status_code=404,
                detail={
                    "message": "Les experts n'ont pas de descriptions ou expertises renseignées",
                    "suggestion": "Complétez les profils"
                }
            )

        expert_embeddings = sentence_model.encode(expert_texts)

        # 4. Recherche des 3 plus proches voisins
        n_neighbors = min(3, len(experts))  # Limité à 3
        distances, indices = knn_model.kneighbors(problem_embedding, n_neighbors=n_neighbors)

        # 5. Formatage des résultats
        results = []
        best_score = 0.0

        for idx, dist in zip(indices[0], distances[0]):
            if idx < len(experts):
                similarity = float(1 - dist)
                if similarity > request.min_similarity:
                    exp = experts[idx]
                    results.append({
                        "firstname": exp.firstname,
                        "lastname": exp.lastname,
                        "email": exp.email,
                        "expertise": exp.expertise,
                        "biographie": exp.biographie,
                        "domaine": exp.domaine,
                        "similarity_score": similarity
                    })
                    best_score = max(best_score, similarity)

        # Trier par score décroissant
        results.sort(key=lambda x: x["similarity_score"], reverse=True)

        if not results:
            raise HTTPException(
                status_code=404,
                detail={
                    "message": "Aucun expert ne correspond à votre problème",
                    "suggestion": "Essayez de reformuler votre demande",
                    "best_score": best_score
                }
            )

        return results

    except Exception as e:
        logger.error(f"Erreur: {str(e)}")
        raise HTTPException(status_code=500, detail="Erreur interne du serveur")


@app.get("/health")
async def health_check():
    """Vérifie l'état du service"""
    return {
        "status": "OK",
        "model_loaded": bool(sentence_model and knn_model),
        "database": "PostgreSQL"
    }