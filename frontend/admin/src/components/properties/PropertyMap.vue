<template>
  <div class="relative w-full h-[calc(100vh-200px)] rounded-lg overflow-hidden border bg-background shadow-lg flex">
    <!-- Sidebar avec liste des propriétés (style Zillow) -->
    <div 
      v-if="showSidebar && !hideSidebar"
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
                v-if="!showSidebar && !hideSidebar"
                variant="default"
                size="icon"
                class="absolute top-4 left-4 z-[1000] bg-white hover:bg-gray-50 text-gray-900 shadow-lg border border-gray-200"
                @click="showSidebar = true"
              >
                <List class="h-4 w-4" />
              </Button>

      <!-- Légende -->
      <div class="absolute top-4 right-4 z-[1000] bg-white/98 backdrop-blur-sm rounded-lg shadow-xl p-4 border border-gray-200 max-w-[220px] max-h-[calc(100vh-120px)] overflow-y-auto">
        <h3 class="text-sm font-semibold mb-3 text-gray-900">{{ t('properties.mapView.legend') }}</h3>
        
        <!-- Propriétés -->
        <div class="mb-4 pb-4 border-b border-gray-200">
          <h4 class="text-xs font-semibold text-gray-600 mb-2 uppercase">Propriétés</h4>
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

        <!-- Points d'intérêt -->
        <div>
          <div class="flex items-center justify-between mb-2">
            <h4 class="text-xs font-semibold text-gray-600 uppercase">Points d'intérêt</h4>
            <Button
              variant="ghost"
              size="sm"
              class="h-6 px-2 text-xs"
              @click="showPOI = !showPOI"
            >
              {{ showPOI ? 'Masquer' : 'Afficher' }}
            </Button>
          </div>
          <div class="space-y-2">
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.bus" @change="updatePOI" class="w-3 h-3" />
              <Bus class="h-3 w-3 text-blue-600" />
              <span class="text-xs text-gray-700">Bus</span>
            </label>
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.train" @change="updatePOI" class="w-3 h-3" />
              <Train class="h-3 w-3 text-green-600" />
              <span class="text-xs text-gray-700">Train</span>
            </label>
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.shop" @change="updatePOI" class="w-3 h-3" />
              <ShoppingBag class="h-3 w-3 text-purple-600" />
              <span class="text-xs text-gray-700">Commerces</span>
            </label>
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.school" @change="updatePOI" class="w-3 h-3" />
              <GraduationCap class="h-3 w-3 text-orange-600" />
              <span class="text-xs text-gray-700">Écoles</span>
            </label>
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.hospital" @change="updatePOI" class="w-3 h-3" />
              <Activity class="h-3 w-3 text-red-600" />
              <span class="text-xs text-gray-700">Hôpitaux</span>
            </label>
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.restaurant" @change="updatePOI" class="w-3 h-3" />
              <UtensilsCrossed class="h-3 w-3 text-yellow-600" />
              <span class="text-xs text-gray-700">Restaurants</span>
            </label>
            <label class="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" v-model="poiTypes.pharmacy" @change="updatePOI" class="w-3 h-3" />
              <Building2 class="h-3 w-3 text-pink-600" />
              <span class="text-xs text-gray-700">Pharmacies</span>
            </label>
          </div>
        </div>
      </div>

              <!-- Compteur de propriétés (si sidebar masquée) -->
              <div v-if="!showSidebar && !hideSidebar" class="absolute top-4 left-14 z-[1000] bg-white/98 backdrop-blur-sm rounded-lg shadow-xl px-4 py-2.5 border border-gray-200">
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
import { List, X, Home, MapPin, Bus, Train, ShoppingBag, GraduationCap, Building2, Activity, UtensilsCrossed } from 'lucide-vue-next'

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
  hideActionButton?: boolean // Pour masquer le bouton "Voir détails" dans le popup
  hideSidebar?: boolean // Pour masquer la sidebar de la liste des propriétés
}

const props = withDefaults(defineProps<Props>(), {
  selectedPropertyId: null,
  hideActionButton: false,
  hideSidebar: false
})

const emit = defineEmits<{
  'property-click': [propertyId: number]
}>()

const { t } = useI18n()
const mapContainer = ref<HTMLElement | null>(null)
// Afficher la sidebar par défaut sur desktop, masquer sur mobile (sauf si hideSidebar est true)
const showSidebar = ref(!props.hideSidebar && window.innerWidth >= 1024)
let map: L.Map | null = null
let markerClusterGroup: L.MarkerClusterGroup | null = null
let markers: L.Marker[] = []
let poiMarkers: L.Marker[] = []
let poiLayerGroup: L.LayerGroup | null = null
const showPOI = ref(true)
const poiTypes = ref({
  bus: true,
  train: true,
  shop: true,
  school: true,
  hospital: true,
  restaurant: true,
  pharmacy: true
})

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

  // Initialiser le groupe de couches pour les POI
  poiLayerGroup = L.layerGroup().addTo(map)
  
  // S'assurer que le layer group est bien visible
  if (poiLayerGroup && map) {
    map.addLayer(poiLayerGroup)
  }

  updateMarkers()
  
  // Attendre un peu avant de charger les POI pour s'assurer que la carte est prête
  setTimeout(() => {
    if (showPOI.value) {
      loadPOI()
    }
  }, 500)
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
    // Utiliser un SVG inline pour l'image placeholder au lieu d'une URL externe
    const placeholderSvg = encodeURIComponent(`
      <svg width="300" height="200" xmlns="http://www.w3.org/2000/svg">
        <rect width="300" height="200" fill="#f3f4f6"/>
        <text x="50%" y="50%" font-family="Arial, sans-serif" font-size="16" fill="#9ca3af" text-anchor="middle" dy=".3em">Aucune image</text>
      </svg>
    `).trim()
    const imageUrl = property.images && property.images.length > 0 
      ? property.images[0] 
      : `data:image/svg+xml,${placeholderSvg}`
    
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
          
          ${!props.hideActionButton ? `
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
          ` : ''}
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

// Créer une icône pour les POI avec le nom affiché directement
const createPOIIcon = (_type: string, color: string, name: string, iconSvg: string, typeName: string) => {
  const iconSize = 28
  const padding = 8
  const textHeight = 20
  const typeIconSize = 16
  const typeHeight = 18
  
  // Créer un élément pour mesurer la largeur du texte
  const tempDiv = document.createElement('div')
  tempDiv.style.position = 'absolute'
  tempDiv.style.visibility = 'hidden'
  tempDiv.style.fontSize = '11px'
  tempDiv.style.fontWeight = '600'
  tempDiv.style.fontFamily = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
  tempDiv.textContent = name
  document.body.appendChild(tempDiv)
  const nameWidth = tempDiv.offsetWidth + padding * 2
  
  // Mesurer la largeur du type avec icône
  tempDiv.style.fontSize = '9px'
  tempDiv.textContent = typeName
  const typeTextWidth = tempDiv.offsetWidth
  document.body.removeChild(tempDiv)
  
  const typeTotalWidth = typeIconSize + 4 + typeTextWidth + padding * 2 // icône + espace + texte + padding
  const textWidth = Math.min(Math.max(nameWidth, typeTotalWidth), 160) // Max 160px
  const totalWidth = Math.max(iconSize, textWidth)
  const totalHeight = iconSize + textHeight + typeHeight + 6
  
  // Utiliser SVG pour l'icône circulaire
  const svgIcon = `
    <div style="
      position: relative;
      width: ${totalWidth}px;
      height: ${totalHeight}px;
      display: flex;
      flex-direction: column;
      align-items: center;
      pointer-events: none;
    ">
      <!-- Icône circulaire -->
      <div style="
        width: ${iconSize}px;
        height: ${iconSize}px;
        background: ${color};
        border: 3px solid white;
        border-radius: 50%;
        box-shadow: 0 2px 6px rgba(0,0,0,0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        position: relative;
        z-index: 2;
      ">
        <div style="
          width: ${iconSize * 0.4}px;
          height: ${iconSize * 0.4}px;
          background: white;
          border-radius: 50%;
        "></div>
      </div>
      <!-- Label avec nom -->
      <div style="
        margin-top: 4px;
        padding: 2px ${padding}px;
        background: white;
        border-radius: 4px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.3);
        font-size: 10px;
        font-weight: 600;
        color: #1f2937;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: ${textWidth}px;
        line-height: 1.2;
        text-align: center;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        position: relative;
        z-index: 1;
      ">
        ${name}
      </div>
      <!-- Icône et nom du type -->
      <div style="
        margin-top: 2px;
        padding: 2px ${padding}px;
        background: ${color};
        border-radius: 3px;
        box-shadow: 0 1px 2px rgba(0,0,0,0.2);
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        max-width: ${textWidth}px;
        position: relative;
        z-index: 1;
      ">
        <div style="
          display: flex;
          align-items: center;
          justify-content: center;
          width: ${typeIconSize}px;
          height: ${typeIconSize}px;
          flex-shrink: 0;
        ">
          ${iconSvg}
        </div>
        <span style="
          font-size: 9px;
          font-weight: 500;
          color: white;
          white-space: nowrap;
          line-height: 1.2;
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        ">
          ${typeName}
        </span>
      </div>
    </div>
  `
  
  return L.divIcon({
    className: 'poi-marker',
    html: svgIcon,
    iconSize: [totalWidth, totalHeight],
    iconAnchor: [totalWidth / 2, totalHeight],
    popupAnchor: [0, -totalHeight]
  })
}

// Configuration des types de POI avec leurs icônes SVG
const poiConfig: Record<string, { color: string; query: string; name: string; icon: string }> = {
  bus: {
    color: '#3b82f6',
    query: 'public_transport=platform;bus=yes',
    name: 'Station de bus',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 6v6"/><path d="M15 6v6"/><path d="M2 12h20"/><path d="M18 18h3s-1-1-1-4h-4s-1 3-1 4"/><path d="M4 18h3s1-1 1-4H4s-1 3-1 4"/><path d="M10 12V8c0-1 1-2 2-2h4c1 0 2 1 2 2v4"/><path d="M9 18H5"/><path d="M19 18h-4"/></svg>'
  },
  train: {
    color: '#10b981',
    query: 'railway=station',
    name: 'Gare',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="3" width="16" height="12" rx="2"/><path d="M4 11h16"/><path d="M12 3v8"/><path d="M8 19l-2 2"/><path d="M18 19l2 2"/><path d="M8 15h.01"/><path d="M16 15h.01"/></svg>'
  },
  shop: {
    color: '#8b5cf6',
    query: 'shop=*',
    name: 'Commerce',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>'
  },
  school: {
    color: '#f97316',
    query: 'amenity=school;amenity=university;amenity=college',
    name: 'École',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>'
  },
  hospital: {
    color: '#ef4444',
    query: 'amenity=hospital;amenity=clinic',
    name: 'Hôpital',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>'
  },
  restaurant: {
    color: '#eab308',
    query: 'amenity=restaurant;amenity=fast_food;amenity=cafe',
    name: 'Restaurant',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2"/><path d="M7 2v20"/><path d="M21 15V2v0a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3v0"/><path d="M21 15v7"/></svg>'
  },
  pharmacy: {
    color: '#ec4899',
    query: 'amenity=pharmacy',
    name: 'Pharmacie',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><path d="M9 3v18"/><path d="M9 12h6"/></svg>'
  }
}

// Récupérer les POI via Overpass API
const loadPOI = async () => {
  if (!map || !poiLayerGroup) {
    console.log('[POI] Carte ou poiLayerGroup non initialisé')
    return
  }
  
  // Permettre le chargement même avec une seule propriété (cas du détail)
  if (propertiesWithLocation.value.length === 0) {
    console.log('[POI] Aucune propriété avec localisation')
    return
  }
  
  console.log(`[POI] Chargement des POI pour ${propertiesWithLocation.value.length} propriété(s)`)

  // Nettoyer les anciens POI
  poiLayerGroup.clearLayers()
  poiMarkers = []

  // Calculer une zone de recherche limitée autour des propriétés
  const bounds = map.getBounds()
  
  // Utiliser les bounds de la carte mais limiter si trop large
  let south = bounds.getSouth()
  let north = bounds.getNorth()
  let west = bounds.getWest()
  let east = bounds.getEast()
  
  // Limiter la zone si elle est trop grande (éviter les requêtes trop lourdes)
  // Réduire la taille maximale pour éviter les timeouts (504)
  const maxSize = 0.03 // ~3.3km max (réduit pour éviter les timeouts)
  if (north - south > maxSize) {
    const center = bounds.getCenter()
    const halfSize = maxSize / 2
    south = center.lat - halfSize
    north = center.lat + halfSize
  }
  if (east - west > maxSize) {
    const center = bounds.getCenter()
    const halfSize = maxSize / 2
    west = center.lng - halfSize
    east = center.lng + halfSize
  }
  
  // Arrondir les coordonnées pour éviter les problèmes de précision
  south = Math.round(south * 100000) / 100000
  north = Math.round(north * 100000) / 100000
  west = Math.round(west * 100000) / 100000
  east = Math.round(east * 100000) / 100000

  // Construire la requête Overpass pour chaque type de POI activé
  const activeTypes = Object.entries(poiTypes.value)
    .filter(([_, enabled]) => enabled)
    .map(([type]) => type)

  if (activeTypes.length === 0) return

  try {
    // Construire la requête Overpass simplifiée (limiter à 100 résultats par type)
    const queries: string[] = []
    
    activeTypes.forEach(type => {
      const config = poiConfig[type]
      if (!config) return
      
      // Construire la requête selon le type avec syntaxe Overpass correcte
      // Format: node[key=value](south,west,north,east);
      if (type === 'bus') {
        queries.push(`node["public_transport"="platform"]["bus"="yes"](${south},${west},${north},${east});`)
      } else if (type === 'train') {
        queries.push(`node["railway"="station"](${south},${west},${north},${east});`)
      } else if (type === 'shop') {
        queries.push(`node["shop"](${south},${west},${north},${east});`)
      } else if (type === 'school') {
        // Utiliser une union pour plusieurs types
        queries.push(`(node["amenity"="school"](${south},${west},${north},${east});node["amenity"="university"](${south},${west},${north},${east});node["amenity"="college"](${south},${west},${north},${east}););`)
      } else if (type === 'hospital') {
        queries.push(`(node["amenity"="hospital"](${south},${west},${north},${east});node["amenity"="clinic"](${south},${west},${north},${east}););`)
      } else if (type === 'restaurant') {
        queries.push(`(node["amenity"="restaurant"](${south},${west},${north},${east});node["amenity"="fast_food"](${south},${west},${north},${east});node["amenity"="cafe"](${south},${west},${north},${east}););`)
      } else if (type === 'pharmacy') {
        queries.push(`node["amenity"="pharmacy"](${south},${west},${north},${east});`)
      }
    })

    if (queries.length === 0) return

    // Construire la requête Overpass avec syntaxe correcte
    // Réduire le timeout pour éviter les timeouts serveur (504)
    const overpassQuery = `[out:json][timeout:10];
(
${queries.join('\n')}
);
out body;`
    
    console.log('[POI] Requête Overpass:', overpassQuery.substring(0, 200) + '...')

    // Liste des serveurs Overpass en fallback
    const overpassServers = [
      'https://overpass-api.de/api/interpreter',
      'https://overpass.kumi.systems/api/interpreter',
      'https://overpass.openstreetmap.fr/api/interpreter'
    ]

    // Appeler l'API Overpass avec retry sur différents serveurs
    let lastError: any = null
    let response: Response | null = null
    
    for (const serverUrl of overpassServers) {
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), 12000) // Timeout de 12 secondes (réduit)

      try {
        console.log(`[POI] Tentative avec serveur: ${serverUrl}`)
        response = await fetch(serverUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `data=${encodeURIComponent(overpassQuery)}`,
          signal: controller.signal
        })

        clearTimeout(timeoutId)

        if (response.ok) {
          console.log(`[POI] Succès avec serveur: ${serverUrl}`)
          break // Succès, sortir de la boucle
        } else if (response.status === 504 || response.status === 429) {
          console.warn(`[POI] Serveur ${serverUrl} surchargé (${response.status})`)
          lastError = new Error(`Serveur surchargé: ${response.status}`)
          // Continuer avec le serveur suivant
          continue
        } else {
          const errorText = await response.text().catch(() => '')
          console.error(`[POI] Erreur ${response.status} avec ${serverUrl}:`, errorText)
          lastError = new Error(`Erreur ${response.status}: ${errorText}`)
          // Continuer avec le serveur suivant
          continue
        }
      } catch (fetchError: any) {
        clearTimeout(timeoutId)
        if (fetchError.name === 'AbortError') {
          console.warn(`[POI] Timeout avec serveur: ${serverUrl}`)
          lastError = fetchError
          // Continuer avec le serveur suivant
          continue
        } else {
          console.error(`[POI] Erreur réseau avec ${serverUrl}:`, fetchError)
          lastError = fetchError
          // Continuer avec le serveur suivant
          continue
        }
      }
    }

    // Si aucun serveur n'a fonctionné
    if (!response || !response.ok) {
      console.error('[POI] Tous les serveurs Overpass ont échoué')
      if (lastError) {
        console.error('[POI] Dernière erreur:', lastError)
      }
      return
    }

    try {

      const data = await response.json()
      
      // Vérifier si la réponse contient une erreur Overpass
      if (data.error) {
        console.warn('[POI] Erreur Overpass:', data.error)
        return
      }
    
    // Traiter les résultats (limiter à 500 résultats côté client)
    if (data.elements && data.elements.length > 0) {
      console.log(`[POI] ${data.elements.length} éléments reçus de l'API Overpass`)
      const limitedElements = data.elements.slice(0, 500)
      let addedCount = 0
      
      limitedElements.forEach((element: any) => {
        // Accepter les nodes et les ways (les ways ont des centres)
        if (element.type === 'node' && element.lat && element.lon) {
          // Déterminer le type de POI
          let poiType: string | null = null
          if (element.tags) {
            if (element.tags.public_transport === 'platform' || element.tags.bus === 'yes') {
              poiType = 'bus'
            } else if (element.tags.railway === 'station') {
              poiType = 'train'
            } else if (element.tags.amenity === 'school' || element.tags.amenity === 'university' || element.tags.amenity === 'college') {
              poiType = 'school'
            } else if (element.tags.amenity === 'hospital' || element.tags.amenity === 'clinic') {
              poiType = 'hospital'
            } else if (element.tags.amenity === 'restaurant' || element.tags.amenity === 'fast_food' || element.tags.amenity === 'cafe') {
              poiType = 'restaurant'
            } else if (element.tags.amenity === 'pharmacy') {
              poiType = 'pharmacy'
            } else if (element.tags.shop) {
              poiType = 'shop'
            }
          }

          // Vérifier si ce type est activé
          if (!poiType || !poiTypes.value[poiType as keyof typeof poiTypes.value]) {
            return
          }

          const config = poiConfig[poiType]
          if (!config) return

          const name = element.tags?.name || config.name
          // Limiter la longueur du nom pour l'affichage
          const displayName = name.length > 15 ? name.substring(0, 15) + '...' : name
          const icon = createPOIIcon(poiType, config.color, displayName, config.icon, config.name)
          
          const marker = L.marker(
            [element.lat, element.lon],
            { 
              icon: icon,
              zIndexOffset: 1000, // S'assurer que les POI sont au-dessus des autres marqueurs
              keyboard: false, // Désactiver le clavier pour éviter les popups
              title: name // Tooltip au survol (optionnel)
            }
          )

          // Ne plus utiliser de popup, l'information est directement sur le marqueur

          if (poiLayerGroup && map) {
            try {
              // Ajouter le marqueur au layer group (qui est déjà sur la carte)
              poiLayerGroup.addLayer(marker)
              poiMarkers.push(marker)
              addedCount++
            } catch (err) {
              console.error('[POI] Erreur lors de l\'ajout du marqueur:', err, marker)
            }
          }
        }
      })
      
      console.log(`[POI] ${addedCount} marqueurs POI ajoutés à la carte`)
      
      // Forcer le rafraîchissement de la carte pour s'assurer que les marqueurs sont visibles
      if (map && addedCount > 0) {
        map.invalidateSize()
        // Vérifier que le layer group est bien sur la carte
        if (poiLayerGroup && !map.hasLayer(poiLayerGroup)) {
          console.warn('[POI] Le poiLayerGroup n\'est pas sur la carte, réajout...')
          poiLayerGroup.addTo(map)
        }
      }
    } else {
      console.log('[POI] Aucun élément reçu de l\'API Overpass')
    }
    } catch (parseError: any) {
      console.error('[POI] Erreur lors du parsing de la réponse:', parseError)
    }
  } catch (error) {
    console.error('Erreur lors du chargement des POI:', error)
  }
}

// Mettre à jour les POI
const updatePOI = () => {
  if (showPOI.value) {
    loadPOI()
  } else {
    if (poiLayerGroup) {
      poiLayerGroup.clearLayers()
      poiMarkers = []
    }
  }
}

// Surveiller les changements de la carte pour recharger les POI
let poiLoadTimeout: ReturnType<typeof setTimeout> | null = null
watch(() => map?.getBounds(), () => {
  if (showPOI.value && map) {
    // Délai pour éviter trop de requêtes (debounce)
    if (poiLoadTimeout) {
      clearTimeout(poiLoadTimeout)
    }
    poiLoadTimeout = setTimeout(() => {
      loadPOI()
    }, 2000) // Augmenter le délai à 2 secondes
  }
}, { deep: true })

watch(showPOI, () => {
  updatePOI()
})

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
  if (poiLayerGroup && map) {
    map.removeLayer(poiLayerGroup)
    poiLayerGroup.clearLayers()
    poiLayerGroup = null
  }
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

/* Styles pour les marqueurs POI */
:deep(.poi-marker) {
  background: transparent !important;
  border: none !important;
  z-index: 1000 !important;
  pointer-events: auto !important;
}

:deep(.poi-marker > div) {
  pointer-events: none !important;
  user-select: none !important;
}

:deep(.poi-marker:hover > div) {
  transform: scale(1.1) !important;
  transition: transform 0.2s ease !important;
}

/* S'assurer que les marqueurs POI sont visibles au-dessus des autres éléments */
:deep(.leaflet-marker-icon.poi-marker) {
  z-index: 1000 !important;
  pointer-events: auto !important;
}

:deep(.leaflet-marker-pane .poi-marker) {
  z-index: 1000 !important;
  pointer-events: auto !important;
}

:deep(.leaflet-marker-pane) {
  z-index: 600 !important;
}

:deep(.poi-popup .leaflet-popup-content-wrapper) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 8px;
}

:deep(.poi-popup .leaflet-popup-content) {
  margin: 0;
  line-height: 1.4;
}
</style>
