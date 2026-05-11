-- Vue musee_avec_region
-- Résout la région normalisée et joint les données démographiques de la table region.
-- Remplace le REGION_JOIN répété dans MuseeStatsDAO.

CREATE OR REPLACE VIEW public.musee_avec_region AS
SELECT
    m.*,
    COALESCE(r.region_musee, m.region)  AS region_resolved,
    COALESCE(r.richesse,       0)        AS richesse,
    COALESCE(r.total_general,  0)        AS total_general,
    COALESCE(r.pop_jeune,      0)        AS pop_jeune
FROM public.musee m
LEFT JOIN (
    SELECT
        total_general,
        richesse,
        COALESCE(age_0_4 + age_5_9 + age_10_14
                 + age_15_19 + age_20_24, 0) AS pop_jeune,
        CASE newreg_l
            WHEN '01 - Guadeloupe'                   THEN 'Guadeloupe'
            WHEN '02 - Martinique'                   THEN 'Martinique'
            WHEN '03 - Guyane'                       THEN 'Guyane'
            WHEN '04 - La Réunion'                   THEN 'La Réunion'
            WHEN '06 - Mayotte'                      THEN 'Mayotte'
            WHEN '11 - Île-de-France'                THEN 'Ile-de-France'
            WHEN '24 - Centre-Val de Loire'          THEN 'Centre-Val de Loire'
            WHEN '27 - Bourgogne-Franche-Comté'      THEN 'Bourgogne-Franche-Comté'
            WHEN '28 - Normandie'                    THEN 'Normandie'
            WHEN '32 - Hauts-de-France'              THEN 'Hauts-de-France'
            WHEN '44 - Grand Est'                    THEN 'Grand Est'
            WHEN '52 - Pays de la Loire'             THEN 'Pays-de-la-Loire'
            WHEN '53 - Bretagne'                     THEN 'Bretagne'
            WHEN '75 - Nouvelle-Aquitaine'           THEN 'Nouvelle-Aquitaine'
            WHEN '76 - Occitanie'                    THEN 'Occitanie'
            WHEN '84 - Auvergne-Rhône-Alpes'         THEN 'Auvergne-Rhône-Alpes'
            WHEN '93 - Provence-Alpes-Côte d''Azur'  THEN 'Provence-Alpes-Côte d''Azur'
            WHEN '94 - Corse'                        THEN 'Corse'
        END AS region_musee
    FROM public.region
) r ON r.region_musee = CASE
    WHEN m.region != 'DROM' THEN m.region
    WHEN m.departement = 'Guadeloupe'  THEN 'Guadeloupe'
    WHEN m.departement = 'Martinique'  THEN 'Martinique'
    WHEN m.departement = 'Guyane'      THEN 'Guyane'
    WHEN m.departement = 'La Réunion'  THEN 'La Réunion'
    WHEN m.departement = 'Réunion'     THEN 'La Réunion'
    WHEN m.departement = 'Mayotte'     THEN 'Mayotte'
    ELSE NULL
END;
