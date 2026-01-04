package com.realestate.document.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Service pour l'optimisation et la conversion d'images
 * Supporte WebP via ImageIO (nécessite une bibliothèque WebP dans le classpath)
 * Utilise sejda-commons 1.1.7 (dernière version disponible)
 */

/**
 * Service pour l'optimisation et la compression d'images
 */
@Service
public class ImageOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageOptimizationService.class);

    @Value("${image.optimization.enabled:true}")
    private boolean optimizationEnabled;

    @Value("${image.optimization.max-width:1920}")
    private int maxWidth;

    @Value("${image.optimization.max-height:1920}")
    private int maxHeight;

    @Value("${image.optimization.quality:0.85}")
    private double quality;

    @Value("${image.optimization.max-file-size-mb:5}")
    private int maxFileSizeMB;

    @Value("${image.optimization.webp.enabled:true}")
    private boolean webpEnabled;

    @Value("${image.optimization.webp.quality:0.80}")
    private double webpQuality;

    /**
     * Optimise une image en réduisant sa taille et en compressant
     * 
     * @param inputStream Le flux d'entrée de l'image originale
     * @param mimeType Le type MIME de l'image (ex: image/jpeg, image/png)
     * @return Un tableau de bytes contenant l'image optimisée
     * @throws IOException Si une erreur survient lors du traitement
     */
    public byte[] optimizeImage(InputStream inputStream, String mimeType) throws IOException {
        // Lire tous les bytes du stream d'abord
        byte[] originalBytes = readAllBytes(inputStream);
        
        if (!optimizationEnabled) {
            logger.debug("Image optimization is disabled, returning original image");
            return originalBytes;
        }

        if (!isImageType(mimeType)) {
            logger.debug("File type {} is not an image, skipping optimization", mimeType);
            return originalBytes;
        }

        try {
            // Lire l'image originale depuis les bytes
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (originalImage == null) {
                logger.warn("Could not read image, returning original bytes");
                return originalBytes;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            logger.debug("Original image dimensions: {}x{}", originalWidth, originalHeight);

            // Déterminer le format de sortie
            String outputFormat = getOutputFormat(mimeType);

            // Vérifier si l'image nécessite un redimensionnement
            boolean needsResize = originalWidth > maxWidth || originalHeight > maxHeight;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (needsResize) {
                // Calculer les nouvelles dimensions en préservant le ratio
                int[] newDimensions = calculateDimensions(originalWidth, originalHeight, maxWidth, maxHeight);
                int newWidth = newDimensions[0];
                int newHeight = newDimensions[1];

                logger.debug("Resizing image to {}x{}", newWidth, newHeight);

                // Redimensionner et compresser
                Thumbnails.of(originalImage)
                        .size(newWidth, newHeight)
                        .outputFormat(outputFormat)
                        .outputQuality(quality)
                        .toOutputStream(outputStream);
            } else {
                // Juste compresser sans redimensionner
                logger.debug("Image dimensions are acceptable, only compressing");
                Thumbnails.of(originalImage)
                        .scale(1.0)
                        .outputFormat(outputFormat)
                        .outputQuality(quality)
                        .toOutputStream(outputStream);
            }

            byte[] optimizedBytes = outputStream.toByteArray();
            long originalSize = originalBytes.length;
            long optimizedSize = optimizedBytes.length;
            double compressionRatio = originalSize > 0 ? ((double) (originalSize - optimizedSize) / originalSize) * 100 : 0;

            logger.info("Image optimized: {} bytes -> {} bytes ({}% reduction)", 
                    originalSize, optimizedSize, String.format("%.2f", compressionRatio));

            // Vérifier si le fichier optimisé est toujours trop volumineux
            if (optimizedSize > maxFileSizeMB * 1024 * 1024) {
                logger.warn("Optimized image still exceeds maximum size ({}MB), applying additional compression", maxFileSizeMB);
                return applyAdditionalCompression(optimizedBytes, outputFormat);
            }

            return optimizedBytes;
        } catch (Exception e) {
            logger.error("Error optimizing image: {}", e.getMessage(), e);
            // En cas d'erreur, retourner l'image originale
            return originalBytes;
        }
    }

    /**
     * Applique une compression supplémentaire si nécessaire
     */
    private byte[] applyAdditionalCompression(byte[] imageBytes, String format) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) {
            return imageBytes;
        }

        // Réduire encore plus la qualité
        double aggressiveQuality = Math.max(0.5, quality - 0.2);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(image)
                .scale(0.8) // Réduire de 20%
                .outputFormat(format)
                .outputQuality(aggressiveQuality)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Calcule les nouvelles dimensions en préservant le ratio d'aspect
     */
    private int[] calculateDimensions(int originalWidth, int originalHeight, int maxWidth, int maxHeight) {
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // S'assurer que les dimensions sont au moins 1
        newWidth = Math.max(1, newWidth);
        newHeight = Math.max(1, newHeight);

        return new int[]{newWidth, newHeight};
    }

    /**
     * Détermine le format de sortie optimal basé sur le type MIME
     */
    private String getOutputFormat(String mimeType) {
        if (mimeType == null) {
            return "jpg";
        }

        // Pour les images avec transparence, utiliser PNG
        if (mimeType.equals("image/png") || mimeType.equals("image/gif")) {
            // Si on veut forcer JPEG pour une meilleure compression, on peut changer ici
            // Pour l'instant, on garde le format original pour préserver la transparence
            return mimeType.equals("image/png") ? "png" : "jpg";
        }

        // Pour JPEG et autres, utiliser JPEG (meilleure compression)
        return "jpg";
    }

    /**
     * Vérifie si le type MIME correspond à une image
     */
    private boolean isImageType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image/");
    }

    /**
     * Vérifie si une image doit être optimisée
     */
    public boolean shouldOptimize(String mimeType, long fileSize) {
        if (!optimizationEnabled) {
            return false;
        }

        if (!isImageType(mimeType)) {
            return false;
        }

        // Optimiser si le fichier dépasse 500KB
        return fileSize > 500 * 1024;
    }

    /**
     * Convertit une image en format WebP
     * 
     * @param inputStream Le flux d'entrée de l'image originale
     * @param mimeType Le type MIME de l'image originale
     * @return Un tableau de bytes contenant l'image en WebP
     * @throws IOException Si une erreur survient lors de la conversion
     */
    public byte[] convertToWebP(InputStream inputStream, String mimeType) throws IOException {
        if (!webpEnabled) {
            logger.debug("WebP conversion is disabled");
            return readAllBytes(inputStream);
        }

        if (!isImageType(mimeType)) {
            logger.debug("File type {} is not an image, cannot convert to WebP", mimeType);
            return readAllBytes(inputStream);
        }

        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                logger.warn("Could not read image for WebP conversion");
                return readAllBytes(inputStream);
            }

            // Redimensionner si nécessaire
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            boolean needsResize = originalWidth > maxWidth || originalHeight > maxHeight;

            if (needsResize) {
                int[] newDimensions = calculateDimensions(originalWidth, originalHeight, maxWidth, maxHeight);
                image = Thumbnails.of(image)
                        .size(newDimensions[0], newDimensions[1])
                        .asBufferedImage();
            }

            // Convertir en WebP
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // Essayer d'utiliser ImageIO avec WebP si disponible (via webp-imageio)
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
                    writer.setOutput(ios);
                    
                    // Configurer les paramètres de qualité WebP si supporté
                    ImageWriteParam writeParam = writer.getDefaultWriteParam();
                    if (writeParam.canWriteCompressed()) {
                        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        // WebP quality: 0.0 (lowest) to 1.0 (highest)
                        writeParam.setCompressionQuality((float) webpQuality);
                    }
                    
                    writer.write(null, new javax.imageio.IIOImage(image, null, null), writeParam);
                } finally {
                    writer.dispose();
                }
            } else {
                // Fallback: utiliser Thumbnailator pour convertir en JPEG optimisé
                // Note: Thumbnailator ne supporte pas WebP nativement
                logger.warn("WebP writer not available (webp-imageio may not be loaded), falling back to optimized JPEG");
                Thumbnails.of(image)
                        .scale(1.0)
                        .outputFormat("jpg")
                        .outputQuality(webpQuality) // Utiliser la même qualité
                        .toOutputStream(outputStream);
            }

            byte[] webpBytes = outputStream.toByteArray();
            logger.info("Image converted to WebP: {} bytes", webpBytes.length);
            return webpBytes;
        } catch (Exception e) {
            logger.error("Error converting image to WebP: {}", e.getMessage(), e);
            // En cas d'erreur, retourner l'image originale
            return readAllBytes(inputStream);
        }
    }

    /**
     * Génère une variante WebP d'une image existante
     * 
     * @param imageBytes Les bytes de l'image originale
     * @param mimeType Le type MIME de l'image originale
     * @return Les bytes de l'image en WebP
     * @throws IOException Si une erreur survient
     */
    public byte[] generateWebPVariant(byte[] imageBytes, String mimeType) throws IOException {
        return convertToWebP(new ByteArrayInputStream(imageBytes), mimeType);
    }

    /**
     * Optimise une image avec une largeur spécifique
     */
    public byte[] optimizeImageWithWidth(InputStream inputStream, String mimeType, int targetWidth) throws IOException {
        byte[] originalBytes = readAllBytes(inputStream);
        
        if (!optimizationEnabled || !isImageType(mimeType)) {
            return originalBytes;
        }

        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (originalImage == null) {
                return originalBytes;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            // Calculer la hauteur proportionnelle
            int targetHeight = (int) ((double) originalHeight * targetWidth / originalWidth);
            
            String outputFormat = getOutputFormat(mimeType);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            Thumbnails.of(originalImage)
                    .size(targetWidth, targetHeight)
                    .outputFormat(outputFormat)
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Error optimizing image with width: {}", e.getMessage(), e);
            return originalBytes;
        }
    }

    /**
     * Vérifie si WebP est supporté par le système
     * Nécessite webp-imageio dans le classpath
     */
    public boolean isWebPSupported() {
        try {
            // Enregistrer le provider WebP si disponible
            // webp-imageio s'enregistre automatiquement via ServiceLoader
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
            boolean supported = writers.hasNext();
            
            if (supported) {
                logger.debug("WebP support is available via webp-imageio");
            } else {
                logger.debug("WebP support is not available. Make sure webp-imageio is in the classpath.");
            }
            
            return supported;
        } catch (Exception e) {
            logger.debug("WebP support check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Initialise le support WebP en enregistrant le provider
     * Appelé automatiquement au démarrage si nécessaire
     */
    public void initializeWebPSupport() {
        try {
            // webp-imageio s'enregistre automatiquement via ServiceLoader
            // Mais on peut forcer l'enregistrement si nécessaire
            String[] mimeTypes = ImageIO.getWriterMIMETypes();
            boolean webpFound = false;
            for (String mimeType : mimeTypes) {
                if (mimeType.equals("image/webp")) {
                    webpFound = true;
                    break;
                }
            }
            
            if (webpFound) {
                logger.info("WebP support initialized successfully");
            } else {
                logger.warn("WebP support not found. WebP conversion will fallback to optimized JPEG. " +
                           "To enable WebP, add a WebP ImageIO library to the classpath.");
            }
        } catch (Exception e) {
            logger.error("Error initializing WebP support: {}", e.getMessage(), e);
        }
    }

    /**
     * Lit tous les bytes d'un InputStream (compatible Java 8+)
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}

