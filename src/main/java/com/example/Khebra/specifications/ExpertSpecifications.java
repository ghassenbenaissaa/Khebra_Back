package com.example.Khebra.specifications;

import com.example.Khebra.entity.Expert;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Types;

public class ExpertSpecifications {


    public static Specification<Expert> hasDomaine(String domaineName) {
        return (root, query, cb) -> {
            if (domaineName == null || domaineName.trim().isEmpty()) {
                return null;
            }
            // Join Expert -> Domaine
            Join<Object, Object> domaineJoin = root.join("domaine");
            return cb.equal(cb.lower(domaineJoin.get("name")), domaineName.toLowerCase());
        };
    }


    public static Specification<Expert> hasRatingBetween(Double minRating, Double maxRating) {
        return (root, query, cb) -> {
            if (minRating == null && maxRating == null) {
                return null;
            } else if (minRating != null && maxRating != null) {
                return cb.between(root.get("rating"), minRating, maxRating);
            } else if (minRating != null) {
                return cb.greaterThanOrEqualTo(root.get("rating"), minRating);
            } else {
                return cb.lessThanOrEqualTo(root.get("rating"), maxRating);
            }
        };
    }

    public static Specification<Expert> hasValidAccount() {
        return (root, query, builder) -> builder.isTrue(root.get("isValidated"));
    }

    public static Specification<Expert> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("IsActive"));
    }

    public static Specification<Expert> isNotBanned() {
        return (root, query, cb) -> cb.isFalse(root.get("isBanned"));
    }

    public static Specification<Expert> hasAdresse(String adresse) {
        return (root, query, cb) ->
                adresse == null || adresse.trim().isEmpty() ? null :
                        cb.equal(cb.lower(root.get("adresse")), adresse.toLowerCase());
    }

    public static Specification<Expert> hasLocationWithinRadius(Double lat, Double lng, Double radiusKm) {
        return (root, query, cb) -> {
            if (lat == null || lng == null || radiusKm == null) {
                return cb.conjunction(); // no filtering
            }

            Expression<String> adresse = root.get("point");

            Expression<Double> expertLat = cb.function("to_number", Double.class,
                    cb.function("SPLIT_PART", String.class, adresse, cb.literal(","), cb.literal("1")),
                    cb.literal("999999.999999"));

            Expression<Double> expertLng = cb.function("to_number", Double.class,
                    cb.function("SPLIT_PART", String.class, adresse, cb.literal(","), cb.literal("2")),
                    cb.literal("999999.999999"));

            Expression<Double> dLat = cb.function("RADIANS", Double.class, cb.diff(expertLat, cb.literal(lat)));
            Expression<Double> dLng = cb.function("RADIANS", Double.class, cb.diff(expertLng, cb.literal(lng)));

            Expression<Double> lat1Rad = cb.function("RADIANS", Double.class, cb.literal(lat));
            Expression<Double> lat2Rad = cb.function("RADIANS", Double.class, expertLat);

            Expression<Double> sinDLatDiv2 = cb.function("SIN", Double.class, cb.quot(dLat, cb.literal(2.0)));
            Expression<Double> sinDLngDiv2 = cb.function("SIN", Double.class, cb.quot(dLng, cb.literal(2.0)));

            Expression<Double> a = cb.sum(
                    cb.prod(sinDLatDiv2, sinDLatDiv2),
                    cb.prod(
                            cb.prod(cb.function("COS", Double.class, lat1Rad),
                                    cb.function("COS", Double.class, lat2Rad)),
                            cb.prod(sinDLngDiv2, sinDLngDiv2)
                    )
            );

            Expression<Double> c = cb.prod(cb.literal(2.0),
                    cb.function("ATAN2", Double.class,
                            cb.function("SQRT", Double.class, a),
                            cb.function("SQRT", Double.class, cb.diff(cb.literal(1.0), a))
                    )
            );

            Expression<Double> distance = cb.prod(cb.literal(6371.0), c);

            return cb.le(distance, radiusKm);
        };
    }

}
