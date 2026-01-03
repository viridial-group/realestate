package com.realestate.document.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

