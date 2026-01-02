<template>
  <div class="relative w-full h-[calc(100vh-200px)] rounded-lg overflow-hidden border bg-background shadow-lg flex">
    <!-- Sidebar avec liste des propriétés (style Zillow) -->
    <div 
      v-if="showSidebar"
      class="w-full lg:w-96 bg-white border-r border-gray-200 overflow-y-auto flex flex-col transition-all duration-300 absolute lg:relative z-[2000] lg:z-auto h-full"
    >
      <!-- Header de la sidebar -->
      <div class="sticky top-0 z-10 bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between">
        <div>
          <h3 class="text-lg font-semibold text-gray-900">
            {{ propertiesWithLocation.length }} {{ t('properties.mapView.propertiesFound') }}
          </h3>
          <p class="text-xs text-gray-500 mt-0.5">{{ t('properties.mapView.clickToView') }}</p>
        </div>
        <Button
          variant="ghost"
          size="icon"
          class="lg:hidden"
          @click="showSidebar = false"
        >
          <X class="h-4 w-4" />
        </Button>
      </div>

      <!-- Liste des propriétés -->
      <div class="flex-1 overflow-y-auto">
        <div
          v-for="property in propertiesWithLocation"
          :key="property.id"
          class="border-b border-gray-100 hover:bg-gray-50 transition-colors cursor-pointer"
          :class="{ 'bg-blue-50 border-l-4 border-l-blue-500': selectedPropertyId === property.id }"
          @click="selectProperty(property.id)"
        >
          <div class="p-4">
            <!-- Image -->
            <div class="relative w-full h-48 rounded-lg overflow-hidden mb-3 bg-gradient-to-br from-gray-200 to-gray-300">
              <img
                v-if="property.images && property.images.length > 0"
                :src="property.images[0]"
                :alt="property.title"
                class="w-full h-full object-cover"
                @error="handleImageError"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
                <Home class="h-12 w-12" />
              </div>
              <!-- Badge de statut -->
              <div
                class="absolute top-2 right-2 px-2 py-1 rounded text-xs font-semibold text-white shadow-lg"
                :style="{ backgroundColor: getStatusColor(property.status) }"
              >
                {{ getStatusLabel(property.status) }}
              </div>
              <!-- Prix -->
              <div class="absolute bottom-2 left-2 px-3 py-1.5 bg-white/95 backdrop-blur-sm rounded-lg shadow-md">
                <span class="text-lg font-bold text-gray-900">{{ formatPrice(property.price) }}</span>
              </div>
            </div>

            <!-- Titre et adresse -->
            <h4 class="font-semibold text-gray-900 mb-1 line-clamp-2 text-sm leading-snug">
              {{ property.title }}
            </h4>
            <p class="text-xs text-gray-500 mb-3 line-clamp-1 flex items-center gap-1">
              <MapPin class="h-3 w-3 flex-shrink-0" />
              {{ property.address }}
            </p>

            <!-- Détails (chambres, salles de bain, surface) -->
            <div class="flex items-center gap-4 text-xs text-gray-600">
              <div v-if="property.bedrooms" class="flex items-center gap-1">
                <span>🛏️</span>
                <span class="font-medium">{{ property.bedrooms }}</span>
              </div>
              <div v-if="property.bathrooms" class="flex items-center gap-1">
                <span>🚿</span>
                <span class="font-medium">{{ property.bathrooms }}</span>
              </div>
              <div v-if="property.area" class="flex items-center gap-1">
                <span>📐</span>
                <span class="font-medium">{{ property.area }} m²</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Zone de la carte -->
    <div class="flex-1 relative w-full h-full">
      <div ref="mapContainer" class="w-full h-full"></div>
      
      <!-- Bouton pour afficher/masquer la sidebar -->
      <Button
        v-if="!showSidebar"
        variant="default"
        size="icon"
        class="absolute top-4 left-4 z-[1000] bg-white hover:bg-gray-50 text-gray-900 shadow-lg border border-gray-200"
        @click="showSidebar = true"
      >
        <List class="h-4 w-4" />
      </Button>

      <!-- Légende -->
      <div class="absolute top-4 right-4 z-[1000] bg-white/98 backdrop-blur-sm rounded-lg shadow-xl p-4 border border-gray-200 max-w-[200px]">
        <h3 class="text-sm font-semibold mb-3 text-gray-900">{{ t('properties.mapView.legend') }}</h3>
        <div class="space-y-2">
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #33d484;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.available') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #fdb022;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.sold') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #04c9ff;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.rented') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #8b5cf6;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.pending') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #6b7280;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.draft') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #10b981;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.published') }}</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 rounded-full flex-shrink-0 shadow-sm" style="background-color: #9ca3af;"></div>
            <span class="text-xs text-gray-700">{{ t('properties.status.archived') }}</span>
          </div>
        </div>
      </div>

      <!-- Compteur de propriétés (si sidebar masquée) -->
      <div v-if="!showSidebar" class="absolute top-4 left-14 z-[1000] bg-white/98 backdrop-blur-sm rounded-lg shadow-xl px-4 py-2.5 border border-gray-200">
        <span class="text-sm font-semibold text-gray-900">
          {{ propertiesWithLocation.length }} {{ t('properties.mapView.propertiesOnMap') }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, onBeforeUnmount } from 'vue'
import { useI18n } from 'vue-i18n'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet.markercluster/dist/MarkerCluster.css'
import 'leaflet.markercluster/dist/MarkerCluster.Default.css'
import * as MarkerClusterGroup from 'leaflet.markercluster'
import type { Property } from '@viridial/shared'
import { Button } from '@/components/ui/button'
import { List, X, Home, MapPin } from 'lucide-vue-next'

// Fix pour les icônes par défaut de Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'
})

interface Props {
  properties: Property[]
  selectedPropertyId?: number | null
}

const props = withDefaults(defineProps<Props>(), {
  selectedPropertyId: null
})

const emit = defineEmits<{
  'property-click': [propertyId: number]
}>()

const { t } = useI18n()
const mapContainer = ref<HTMLElement | null>(null)
// Afficher la sidebar par défaut sur desktop, masquer sur mobile
const showSidebar = ref(window.innerWidth >= 1024)
let map: L.Map | null = null
let markerClusterGroup: L.MarkerClusterGroup | null = null
let markers: L.Marker[] = []

// Filtrer les propriétés qui ont des coordonnées
const propertiesWithLocation = computed(() => {
  return props.properties.filter(
    p => p.latitude != null && p.longitude != null && 
         !isNaN(p.latitude) && !isNaN(p.longitude)
  )
})

// Couleurs selon le statut
const getStatusColor = (status: string): string => {
  const colors: Record<string, string> = {
    'AVAILABLE': '#33d484',
    'SOLD': '#fdb022',
    'RENTED': '#04c9ff',
    'PENDING': '#8b5cf6',
    'DRAFT': '#6b7280',
    'PUBLISHED': '#10b981',
    'ARCHIVED': '#9ca3af'
  }
  return colors[status] || '#6b7280'
}

// Créer un marqueur en forme de pin (style Zillow)
const createPinIcon = (status: string, isSelected: boolean = false) => {
  const color = getStatusColor(status)
  const scale = isSelected ? 1.3 : 1
  const width = 28 * scale
  const height = 40 * scale
  
  return L.divIcon({
    className: 'custom-pin-marker',
    html: `
      <div style="
        width: ${width}px;
        height: ${height}px;
        position: relative;
        transform: translate(-50%, -100%);
        cursor: pointer;
        transition: transform 0.2s ease;
      ">
        <svg width="${width}" height="${height}" viewBox="0 0 28 40" style="filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));">
          <path d="M14 0C6.268 0 0 6.268 0 14c0 10.5 14 26 14 26s14-15.5 14-26C28 6.268 21.732 0 14 0z" 
                fill="${color}" 
                stroke="white" 
                stroke-width="2"/>
          <circle cx="14" cy="14" r="6" fill="white" opacity="0.9"/>
        </svg>
        ${isSelected ? `
          <div style="
            position: absolute;
            top: -8px;
            left: 50%;
            transform: translateX(-50%);
            width: 12px;
            height: 12px;
            background: ${color};
            border: 3px solid white;
            border-radius: 50%;
            box-shadow: 0 2px 8px rgba(0,0,0,0.4);
          "></div>
        ` : ''}
      </div>
    `,
    iconSize: [width, height],
    iconAnchor: [width / 2, height],
    popupAnchor: [0, -height - 5]
  })
}

// Initialiser la carte
const initMap = () => {
  if (!mapContainer.value) return

  // Créer la carte avec un style plus moderne
  map = L.map(mapContainer.value, {
    zoomControl: false, // On ajoutera nos propres contrôles
    attributionControl: true,
    scrollWheelZoom: true,
    doubleClickZoom: true,
    boxZoom: true,
    keyboard: true
  })

  // Ajouter le tile layer avec un style moderne (style Zillow-like)
  L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '© OpenStreetMap contributors © CARTO',
    maxZoom: 19,
    subdomains: 'abcd'
  }).addTo(map)

  // Alternative: Style plus coloré comme Zillow (décommentez pour activer)
  // L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  //   attribution: '© OpenStreetMap contributors',
  //   maxZoom: 19
  // }).addTo(map)

  // Ajouter les contrôles de zoom personnalisés
  L.control.zoom({
    position: 'bottomright'
  }).addTo(map)

  // Centrer la carte sur les propriétés ou sur la France par défaut
  if (propertiesWithLocation.value.length > 0) {
    const bounds = L.latLngBounds(
      propertiesWithLocation.value.map(p => [p.latitude!, p.longitude!])
    )
    map.fitBounds(bounds, { padding: [80, 80] })
  } else {
    map.setView([46.6034, 1.8883], 6) // Centre de la France
  }

  // Initialiser le cluster group
  markerClusterGroup = new (MarkerClusterGroup as any).MarkerClusterGroup({
    chunkedLoading: true,
    chunkDelay: 100,
    spiderfyOnMaxZoom: true,
    showCoverageOnHover: false,
    zoomToBoundsOnClick: true,
    maxClusterRadius: 50,
    iconCreateFunction: (cluster: any) => {
      const count = cluster.getChildCount()
      const size = count < 10 ? 'small' : count < 100 ? 'medium' : 'large'
      return L.divIcon({
        html: `<div class="marker-cluster marker-cluster-${size}">${count}</div>`,
        className: 'marker-cluster-container',
        iconSize: L.point(40, 40)
      })
    }
  })

  updateMarkers()
}

// Mettre à jour les marqueurs
const updateMarkers = () => {
  if (!map || !markerClusterGroup) return

  // Supprimer les anciens marqueurs
  markerClusterGroup.clearLayers()
  markers = []

  // Ajouter les nouveaux marqueurs
  propertiesWithLocation.value.forEach(property => {
    const isSelected = props.selectedPropertyId === property.id
    const marker = L.marker(
      [property.latitude!, property.longitude!],
      { icon: createPinIcon(property.status, isSelected) }
    )

    // Créer le popup amélioré style Zillow
    const imageUrl = property.images && property.images.length > 0 
      ? property.images[0] 
      : 'https://via.placeholder.com/300x200?text=No+Image'
    
    const popupContent = `
      <div class="property-popup" style="
        min-width: 280px;
        max-width: 320px;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
        margin: 0;
      ">
        <!-- Image -->
        <div style="
          width: 100%;
          height: 180px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 8px 8px 0 0;
          overflow: hidden;
          position: relative;
          margin: -10px -10px 12px -10px;
        ">
          <img 
            src="${imageUrl}" 
            alt="${property.title}"
            style="
              width: 100%;
              height: 100%;
              object-fit: cover;
            "
            onerror="this.style.display='none'; this.parentElement.style.background='linear-gradient(135deg, #667eea 0%, #764ba2 100%)';"
          />
          <div style="
            position: absolute;
            top: 8px;
            right: 8px;
            background: rgba(255,255,255,0.95);
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 600;
            color: ${getStatusColor(property.status)};
          ">
            ${getStatusLabel(property.status)}
          </div>
        </div>
        
        <!-- Contenu -->
        <div style="padding: 0 4px;">
          <h3 style="
            font-weight: 600;
            font-size: 16px;
            margin: 0 0 6px 0;
            color: #1f2937;
            line-height: 1.3;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          ">
            ${property.title}
          </h3>
          
          <p style="
            font-size: 13px;
            color: #6b7280;
            margin: 0 0 12px 0;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            overflow: hidden;
          ">
            📍 ${property.address}
          </p>
          
          <!-- Prix et détails -->
          <div style="
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 12px;
            padding-bottom: 12px;
            border-bottom: 1px solid #e5e7eb;
          ">
            <div>
              <div style="
                font-size: 20px;
                font-weight: 700;
                color: #059669;
                margin-bottom: 4px;
              ">
                ${formatPrice(property.price)}
              </div>
              ${property.area ? `
                <div style="font-size: 12px; color: #6b7280;">
                  ${property.area} m²
                </div>
              ` : ''}
            </div>
            
            <div style="
              display: flex;
              gap: 12px;
              align-items: center;
            ">
              ${property.bedrooms ? `
                <div style="
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  gap: 2px;
                ">
                  <span style="font-size: 18px;">🛏️</span>
                  <span style="font-size: 11px; color: #6b7280; font-weight: 500;">
                    ${property.bedrooms}
                  </span>
                </div>
              ` : ''}
              ${property.bathrooms ? `
                <div style="
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  gap: 2px;
                ">
                  <span style="font-size: 18px;">🚿</span>
                  <span style="font-size: 11px; color: #6b7280; font-weight: 500;">
                    ${property.bathrooms}
                  </span>
                </div>
              ` : ''}
            </div>
          </div>
          
          <!-- Bouton d'action -->
          <button 
            onclick="window.dispatchEvent(new CustomEvent('property-click', { detail: ${property.id} }))"
            style="
              width: 100%;
              padding: 10px;
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
              border: none;
              border-radius: 6px;
              font-size: 14px;
              font-weight: 600;
              cursor: pointer;
              transition: transform 0.2s, box-shadow 0.2s;
              box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            "
            onmouseover="this.style.transform='translateY(-1px)'; this.style.boxShadow='0 4px 8px rgba(0,0,0,0.15)';"
            onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 2px 4px rgba(0,0,0,0.1)';"
          >
            ${t('common.view')} ${t('common.details')}
          </button>
        </div>
      </div>
    `

    marker.bindPopup(popupContent, {
      className: 'custom-popup',
      maxWidth: 320,
      closeButton: true,
      autoPan: true,
      autoPanPadding: [50, 50]
    })

    marker.on('click', () => {
      emit('property-click', property.id)
    })

    marker.on('mouseover', () => {
      marker.openPopup()
    })

    if (markerClusterGroup) {
      markerClusterGroup.addLayer(marker)
    }
    markers.push(marker)
  })

  // Ajouter le cluster group à la carte
  if (markerClusterGroup && map) {
    markerClusterGroup.addTo(map)
  }
}

// Formater le prix
const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price)
}

// Obtenir le label du statut
const getStatusLabel = (status: string): string => {
  return t(`properties.status.${status.toLowerCase()}`)
}

// Sélectionner une propriété depuis la sidebar
const selectProperty = (propertyId: number) => {
  emit('property-click', propertyId)
  
  // Centrer la carte sur la propriété sélectionnée
  if (map) {
    const property = propertiesWithLocation.value.find(p => p.id === propertyId)
    if (property) {
      map.setView([property.latitude!, property.longitude!], 16, {
        animate: true,
        duration: 0.5
      })
      // Ouvrir le popup
      setTimeout(() => {
        const marker = markers.find((_, index) => 
          propertiesWithLocation.value[index].id === propertyId
        )
        if (marker) {
          marker.openPopup()
        }
      }, 500)
    }
  }
}

// Gérer les erreurs d'image
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// Gérer le redimensionnement de la fenêtre
const handleResize = () => {
  if (window.innerWidth < 1024) {
    showSidebar.value = false
  } else if (window.innerWidth >= 1024 && !showSidebar.value) {
    // Optionnel: réafficher la sidebar sur desktop si elle était masquée
    // showSidebar.value = true
  }
  
  // Redimensionner la carte si nécessaire
  if (map) {
    setTimeout(() => {
      map?.invalidateSize()
    }, 100)
  }
}

// Écouter les événements depuis les popups
onMounted(() => {
  initMap()
  
  // Écouter les clics depuis les popups
  window.addEventListener('property-click', ((e: CustomEvent) => {
    emit('property-click', e.detail)
  }) as EventListener)
  
  // Écouter le redimensionnement
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (markerClusterGroup && map) {
    map.removeLayer(markerClusterGroup)
    markerClusterGroup.clearLayers()
    markerClusterGroup = null
  }
  if (map) {
    map.remove()
    map = null
  }
  window.removeEventListener('property-click', (() => {}) as EventListener)
  window.removeEventListener('resize', handleResize)
})

// Watchers
watch(() => props.properties, () => {
  if (map) {
    updateMarkers()
    // Recentrer la carte si nécessaire
    if (propertiesWithLocation.value.length > 0) {
      const bounds = L.latLngBounds(
        propertiesWithLocation.value.map(p => [p.latitude!, p.longitude!])
      )
      map.fitBounds(bounds, { padding: [80, 80] })
    }
  }
}, { deep: true })

watch(() => props.selectedPropertyId, () => {
  updateMarkers()
  // Centrer la carte sur la propriété sélectionnée
  if (props.selectedPropertyId && map) {
    const selectedProperty = propertiesWithLocation.value.find(
      p => p.id === props.selectedPropertyId
    )
    if (selectedProperty) {
      map.setView([selectedProperty.latitude!, selectedProperty.longitude!], 16, {
        animate: true,
        duration: 0.5
      })
      // Ouvrir le popup après un court délai
      setTimeout(() => {
        const marker = markers.find((_, index) => 
          propertiesWithLocation.value[index].id === selectedProperty.id
        )
        if (marker) {
          marker.openPopup()
        }
      }, 500)
    }
  }
})
</script>

<style scoped>
:deep(.custom-popup) {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

:deep(.custom-popup .leaflet-popup-content-wrapper) {
  border-radius: 12px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  padding: 0;
  overflow: hidden;
}

:deep(.custom-popup .leaflet-popup-content) {
  margin: 10px;
  line-height: 1.5;
}

:deep(.custom-popup .leaflet-popup-tip) {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.custom-pin-marker) {
  background: transparent;
  border: none;
}

:deep(.custom-pin-marker:hover) {
  transform: translate(-50%, -100%) scale(1.1) !important;
  transition: transform 0.2s ease;
}

/* Styles pour les clusters */
:deep(.marker-cluster-container) {
  background: transparent !important;
}

:deep(.marker-cluster) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  border-radius: 50% !important;
  font-weight: 700 !important;
  font-size: 14px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2) !important;
  border: 3px solid white !important;
}

:deep(.marker-cluster-small) {
  width: 40px !important;
  height: 40px !important;
  font-size: 12px !important;
}

:deep(.marker-cluster-medium) {
  width: 50px !important;
  height: 50px !important;
  font-size: 14px !important;
}

:deep(.marker-cluster-large) {
  width: 60px !important;
  height: 60px !important;
  font-size: 16px !important;
}

/* Améliorer les contrôles de zoom */
:deep(.leaflet-control-zoom) {
  border: none !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15) !important;
  border-radius: 8px !important;
  overflow: hidden;
}

:deep(.leaflet-control-zoom a) {
  background: white !important;
  color: #374151 !important;
  border: none !important;
  width: 36px !important;
  height: 36px !important;
  line-height: 36px !important;
  font-size: 18px !important;
  transition: background-color 0.2s !important;
}

:deep(.leaflet-control-zoom a:hover) {
  background: #f3f4f6 !important;
  color: #111827 !important;
}

:deep(.leaflet-control-zoom-in) {
  border-bottom: 1px solid #e5e7eb !important;
}
</style>
