package com.realestate.property.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une transaction DVF (Demandes de Valeurs Foncières)
 * Source: data.gouv.fr - DGFiP
 */
@Entity
@Table(name = "dvf_transactions", indexes = {
    @Index(name = "idx_dvf_mutation_date", columnList = "mutation_date"),
    @Index(name = "idx_dvf_commune", columnList = "commune_code"),
    @Index(name = "idx_dvf_type_local", columnList = "type_local"),
    @Index(name = "idx_dvf_surface", columnList = "surface_reelle_bati"),
    @Index(name = "idx_dvf_prix", columnList = "valeur_fonciere"),
    @Index(name = "idx_dvf_location", columnList = "latitude,longitude")
})
public class DVFTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "mutation_date", nullable = false)
    private LocalDate mutationDate; // Date de mutation

    @Column(name = "nature_mutation", length = 50)
    private String natureMutation; // Vente, Vente en l'état futur d'achèvement, etc.

    @Column(name = "valeur_fonciere", precision = 15, scale = 2)
    private BigDecimal valeurFonciere; // Prix de vente

    @Column(name = "numero_disposition")
    private Integer numeroDisposition;

    @Column(name = "lot1_numero")
    private String lot1Numero;

    @Column(name = "lot1_surface_carrez")
    private BigDecimal lot1SurfaceCarrez;

    @Column(name = "lot2_numero")
    private String lot2Numero;

    @Column(name = "lot2_surface_carrez")
    private BigDecimal lot2SurfaceCarrez;

    @Column(name = "lot3_numero")
    private String lot3Numero;

    @Column(name = "lot3_surface_carrez")
    private BigDecimal lot3SurfaceCarrez;

    @Column(name = "lot4_numero")
    private String lot4Numero;

    @Column(name = "lot4_surface_carrez")
    private BigDecimal lot4SurfaceCarrez;

    @Column(name = "lot5_numero")
    private String lot5Numero;

    @Column(name = "lot5_surface_carrez")
    private BigDecimal lot5SurfaceCarrez;

    @Column(name = "nombre_lots")
    private Integer nombreLots;

    @Column(name = "code_type_local")
    private Integer codeTypeLocal; // 1=Maison, 2=Appartement, 3=Dépendance, 4=Local industriel/commercial

    @Column(name = "type_local", length = 50)
    private String typeLocal; // Maison, Appartement, etc.

    @Column(name = "surface_reelle_bati")
    private BigDecimal surfaceReelleBati; // Surface en m²

    @Column(name = "nombre_pieces_principales")
    private Integer nombrePiecesPrincipales;

    @Column(name = "code_nature_culture")
    private String codeNatureCulture;

    @Column(name = "nature_culture", length = 100)
    private String natureCulture;

    @Column(name = "code_nature_culture_speciale")
    private String codeNatureCultureSpeciale;

    @Column(name = "nature_culture_speciale", length = 100)
    private String natureCultureSpeciale;

    @Column(name = "surface_terrain")
    private BigDecimal surfaceTerrain; // Surface du terrain en m²

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "code_voie", length = 10)
    private String codeVoie;

    @Column(name = "type_voie", length = 50)
    private String typeVoie; // Rue, Avenue, etc.

    @Column(name = "voie", length = 255)
    private String voie; // Nom de la voie

    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Column(name = "commune", length = 255)
    private String commune; // Nom de la commune

    @Column(name = "commune_code", length = 10)
    private String communeCode; // Code INSEE

    @Column(name = "code_departement", length = 5)
    private String codeDepartement;

    @Column(name = "ancien_code_commune", length = 10)
    private String ancienCodeCommune;

    @Column(name = "ancien_nom_commune", length = 255)
    private String ancienNomCommune;

    @Column(name = "id_parcelle", length = 50)
    private String idParcelle; // Identifiant cadastral

    @Column(name = "ancien_id_parcelle", length = 50)
    private String ancienIdParcelle;

    @Column(name = "numero_volume", length = 10)
    private String numeroVolume;

    @Column(name = "lot", length = 10)
    private String lot;

    @Column(name = "prix_metre_carre")
    private BigDecimal prixMetreCarre; // Prix calculé au m²

    @Column(name = "millesime", length = 10)
    private String millesime; // Année du fichier DVF

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public DVFTransaction() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getMutationDate() {
        return mutationDate;
    }

    public void setMutationDate(LocalDate mutationDate) {
        this.mutationDate = mutationDate;
    }

    public String getNatureMutation() {
        return natureMutation;
    }

    public void setNatureMutation(String natureMutation) {
        this.natureMutation = natureMutation;
    }

    public BigDecimal getValeurFonciere() {
        return valeurFonciere;
    }

    public void setValeurFonciere(BigDecimal valeurFonciere) {
        this.valeurFonciere = valeurFonciere;
    }

    public Integer getNumeroDisposition() {
        return numeroDisposition;
    }

    public void setNumeroDisposition(Integer numeroDisposition) {
        this.numeroDisposition = numeroDisposition;
    }

    public String getLot1Numero() {
        return lot1Numero;
    }

    public void setLot1Numero(String lot1Numero) {
        this.lot1Numero = lot1Numero;
    }

    public BigDecimal getLot1SurfaceCarrez() {
        return lot1SurfaceCarrez;
    }

    public void setLot1SurfaceCarrez(BigDecimal lot1SurfaceCarrez) {
        this.lot1SurfaceCarrez = lot1SurfaceCarrez;
    }

    public String getLot2Numero() {
        return lot2Numero;
    }

    public void setLot2Numero(String lot2Numero) {
        this.lot2Numero = lot2Numero;
    }

    public BigDecimal getLot2SurfaceCarrez() {
        return lot2SurfaceCarrez;
    }

    public void setLot2SurfaceCarrez(BigDecimal lot2SurfaceCarrez) {
        this.lot2SurfaceCarrez = lot2SurfaceCarrez;
    }

    public String getLot3Numero() {
        return lot3Numero;
    }

    public void setLot3Numero(String lot3Numero) {
        this.lot3Numero = lot3Numero;
    }

    public BigDecimal getLot3SurfaceCarrez() {
        return lot3SurfaceCarrez;
    }

    public void setLot3SurfaceCarrez(BigDecimal lot3SurfaceCarrez) {
        this.lot3SurfaceCarrez = lot3SurfaceCarrez;
    }

    public String getLot4Numero() {
        return lot4Numero;
    }

    public void setLot4Numero(String lot4Numero) {
        this.lot4Numero = lot4Numero;
    }

    public BigDecimal getLot4SurfaceCarrez() {
        return lot4SurfaceCarrez;
    }

    public void setLot4SurfaceCarrez(BigDecimal lot4SurfaceCarrez) {
        this.lot4SurfaceCarrez = lot4SurfaceCarrez;
    }

    public String getLot5Numero() {
        return lot5Numero;
    }

    public void setLot5Numero(String lot5Numero) {
        this.lot5Numero = lot5Numero;
    }

    public BigDecimal getLot5SurfaceCarrez() {
        return lot5SurfaceCarrez;
    }

    public void setLot5SurfaceCarrez(BigDecimal lot5SurfaceCarrez) {
        this.lot5SurfaceCarrez = lot5SurfaceCarrez;
    }

    public Integer getNombreLots() {
        return nombreLots;
    }

    public void setNombreLots(Integer nombreLots) {
        this.nombreLots = nombreLots;
    }

    public Integer getCodeTypeLocal() {
        return codeTypeLocal;
    }

    public void setCodeTypeLocal(Integer codeTypeLocal) {
        this.codeTypeLocal = codeTypeLocal;
    }

    public String getTypeLocal() {
        return typeLocal;
    }

    public void setTypeLocal(String typeLocal) {
        this.typeLocal = typeLocal;
    }

    public BigDecimal getSurfaceReelleBati() {
        return surfaceReelleBati;
    }

    public void setSurfaceReelleBati(BigDecimal surfaceReelleBati) {
        this.surfaceReelleBati = surfaceReelleBati;
    }

    public Integer getNombrePiecesPrincipales() {
        return nombrePiecesPrincipales;
    }

    public void setNombrePiecesPrincipales(Integer nombrePiecesPrincipales) {
        this.nombrePiecesPrincipales = nombrePiecesPrincipales;
    }

    public String getCodeNatureCulture() {
        return codeNatureCulture;
    }

    public void setCodeNatureCulture(String codeNatureCulture) {
        this.codeNatureCulture = codeNatureCulture;
    }

    public String getNatureCulture() {
        return natureCulture;
    }

    public void setNatureCulture(String natureCulture) {
        this.natureCulture = natureCulture;
    }

    public String getCodeNatureCultureSpeciale() {
        return codeNatureCultureSpeciale;
    }

    public void setCodeNatureCultureSpeciale(String codeNatureCultureSpeciale) {
        this.codeNatureCultureSpeciale = codeNatureCultureSpeciale;
    }

    public String getNatureCultureSpeciale() {
        return natureCultureSpeciale;
    }

    public void setNatureCultureSpeciale(String natureCultureSpeciale) {
        this.natureCultureSpeciale = natureCultureSpeciale;
    }

    public BigDecimal getSurfaceTerrain() {
        return surfaceTerrain;
    }

    public void setSurfaceTerrain(BigDecimal surfaceTerrain) {
        this.surfaceTerrain = surfaceTerrain;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getCodeVoie() {
        return codeVoie;
    }

    public void setCodeVoie(String codeVoie) {
        this.codeVoie = codeVoie;
    }

    public String getTypeVoie() {
        return typeVoie;
    }

    public void setTypeVoie(String typeVoie) {
        this.typeVoie = typeVoie;
    }

    public String getVoie() {
        return voie;
    }

    public void setVoie(String voie) {
        this.voie = voie;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getCommuneCode() {
        return communeCode;
    }

    public void setCommuneCode(String communeCode) {
        this.communeCode = communeCode;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getAncienCodeCommune() {
        return ancienCodeCommune;
    }

    public void setAncienCodeCommune(String ancienCodeCommune) {
        this.ancienCodeCommune = ancienCodeCommune;
    }

    public String getAncienNomCommune() {
        return ancienNomCommune;
    }

    public void setAncienNomCommune(String ancienNomCommune) {
        this.ancienNomCommune = ancienNomCommune;
    }

    public String getIdParcelle() {
        return idParcelle;
    }

    public void setIdParcelle(String idParcelle) {
        this.idParcelle = idParcelle;
    }

    public String getAncienIdParcelle() {
        return ancienIdParcelle;
    }

    public void setAncienIdParcelle(String ancienIdParcelle) {
        this.ancienIdParcelle = ancienIdParcelle;
    }

    public String getNumeroVolume() {
        return numeroVolume;
    }

    public void setNumeroVolume(String numeroVolume) {
        this.numeroVolume = numeroVolume;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public BigDecimal getPrixMetreCarre() {
        return prixMetreCarre;
    }

    public void setPrixMetreCarre(BigDecimal prixMetreCarre) {
        this.prixMetreCarre = prixMetreCarre;
    }

    public String getMillesime() {
        return millesime;
    }

    public void setMillesime(String millesime) {
        this.millesime = millesime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Calcule le prix au m² si la surface et la valeur foncière sont disponibles
     */
    @PrePersist
    @PreUpdate
    public void calculatePrixMetreCarre() {
        if (valeurFonciere != null && surfaceReelleBati != null && surfaceReelleBati.compareTo(BigDecimal.ZERO) > 0) {
            this.prixMetreCarre = valeurFonciere.divide(surfaceReelleBati, 2, java.math.RoundingMode.HALF_UP);
        }
    }
}

