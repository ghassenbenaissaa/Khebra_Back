from pydantic import BaseModel
from typing import Optional

class ExpertOut(BaseModel):
    firstname: str
    lastname: str
    email: str
    expertise: str
    biographie: str
    domaine: str
    similarity_score: float

class RecommendationRequest(BaseModel):
    problem: str
    top_k: int = 3
    min_similarity: float = 0.45

class NoExpertResponse(BaseModel):
    message: str
    suggestion: str
    best_score: Optional[float] = None