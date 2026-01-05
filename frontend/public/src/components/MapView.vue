<script setup lang="ts">
    import { onMounted, watch, ref } from 'vue'
    import L from 'leaflet'
    import 'leaflet/dist/leaflet.css'
    
    const props = defineProps<{
      listings: {
        id: number
        title: string
        city: string
        type: string
        status: string
        price: number
        surface: number
        lat: number
        lng: number
      }[]
      interests?: {
        id: number
        name: string
        type: string
        lat: number
        lng: number
        icon: string
      }[]
      highlightedListingId?: number | null
    }>()
    
    const emit = defineEmits<{
      'marker-click': [id: number]
      'center-on-listing': [id: number]
      'user-location-updated': [location: { lat: number; lng: number }]
    }>()
    
    // Map & markers
    const map = ref<L.Map | null>(null)
    let markersLayer: L.LayerGroup
    let poiLayer: L.LayerGroup
    let userLocationMarker: L.Marker | null = null
    const userLocation = ref<{ lat: number; lng: number } | null>(null)
    const locationError = ref<string | null>(null)
    
    // Initialisation map
    onMounted(() => {
      map.value = L.map('map-view-container').setView([46.603354, 1.888334], 6)
    
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap',
      }).addTo(map.value! as any)
    
      markersLayer = L.layerGroup().addTo(map.value! as any)
      poiLayer = L.layerGroup().addTo(map.value! as any)
      renderMarkers()
      renderPOIs()
      
      // Demander la géolocalisation
      getUserLocation()
    })
    
    // Fonction pour obtenir la position de l'utilisateur
    function getUserLocation() {
      if (!navigator.geolocation) {
        locationError.value = 'La géolocalisation n\'est pas supportée par votre navigateur'
        return
      }
      
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude
          const lng = position.coords.longitude
          userLocation.value = { lat, lng }
          locationError.value = null
          
          // Afficher le marker de l'utilisateur
          displayUserLocation(lat, lng)
          
          // Émettre l'événement pour mettre à jour les POI
          emit('user-location-updated', { lat, lng })
        },
        (error) => {
          console.error('Erreur de géolocalisation:', error)
          switch (error.code) {
            case error.PERMISSION_DENIED:
              locationError.value = 'Permission de géolocalisation refusée'
              break
            case error.POSITION_UNAVAILABLE:
              locationError.value = 'Position indisponible'
              break
            case error.TIMEOUT:
              locationError.value = 'Timeout de la requête de géolocalisation'
              break
            default:
              locationError.value = 'Erreur inconnue lors de la géolocalisation'
              break
          }
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        }
      )
    }
    
    // Fonction pour afficher la position de l'utilisateur sur la map
    function displayUserLocation(lat: number, lng: number) {
      if (!map.value) return
      
      // Supprimer l'ancien marker s'il existe
      if (userLocationMarker) {
        map.value.removeLayer(userLocationMarker)
      }
      
      // Créer une icône personnalisée pour la position de l'utilisateur
      const userIcon = L.divIcon({
        className: 'user-location-marker',
        html: `
          <div style="
            position: relative;
            width: 24px;
            height: 24px;
          ">
            <div style="
              position: absolute;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              width: 20px;
              height: 20px;
              background: #4285f4;
              border: 3px solid white;
              border-radius: 50%;
              box-shadow: 0 2px 8px rgba(0,0,0,0.3);
            "></div>
            <div style="
              position: absolute;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              width: 40px;
              height: 40px;
              background: #4285f4;
              opacity: 0.2;
              border-radius: 50%;
              animation: pulse 2s infinite;
            "></div>
          </div>
        `,
        iconSize: [24, 24],
        iconAnchor: [12, 12]
      })
      
      // Créer le marker
      userLocationMarker = L.marker([lat, lng], { 
        icon: userIcon,
        zIndexOffset: 1000 // Au-dessus des autres markers
      })
        .bindPopup(`
          <div style="min-width: 180px; text-align: center;">
            <strong style="color: #4285f4;">📍 Votre position</strong><br>
            <span style="color: #5f6368; font-size: 12px;">
              ${lat.toFixed(6)}, ${lng.toFixed(6)}
            </span>
          </div>
        `)
        .addTo(map.value! as any)
      
      // Centrer et zoomer sur la position de l'utilisateur (niveau ville)
      if (map.value) {
        map.value.setView([lat, lng], 13, { animate: true })
      }
    }
    
    // Expose method to center on user location
    function centerOnUserLocation() {
      if (userLocation.value && map.value) {
        // Zoom niveau ville (13) pour voir la zone autour de la position
        map.value.setView([userLocation.value.lat, userLocation.value.lng], 13, { animate: true })
      } else {
        getUserLocation()
      }
    }
    
    // Watchers corrects
    watch(
      () => props.listings, // ✅ getter function
      () => renderMarkers(),
      { deep: true }
    )
    
    watch(
      () => props.interests,
      () => renderPOIs(),
      { deep: true }
    )
    
    watch(
      () => props.highlightedListingId,
      (listingId) => {
        if (listingId && map.value) {
          const listing = props.listings.find(l => l.id === listingId)
          if (listing) {
            map.value.setView([listing.lat, listing.lng], 15, { animate: true })
          }
        }
      }
    )
    
    // Expose method to center on POI
    function centerOnPOI(lat: number, lng: number) {
      if (map.value) {
        map.value.setView([lat, lng], 16, { animate: true })
      }
    }
    
    // Expose method to center on listing
    function centerOnListing(listingId: number) {
      const listing = props.listings.find(l => l.id === listingId)
      if (listing && map.value) {
        map.value.setView([listing.lat, listing.lng], 15, { animate: true })
        emit('center-on-listing', listingId)
      }
    }
    
    // Expose method to invalidate map size (useful after DOM changes)
    function invalidateSize() {
      if (map.value) {
        setTimeout(() => {
          map.value?.invalidateSize()
        }, 100)
      }
    }
    
    defineExpose({
      centerOnPOI,
      centerOnListing,
      centerOnUserLocation,
      getUserLocation,
      invalidateSize
    })
    
    // Fonction pour obtenir l'icône selon le type de bien
    function getListingIcon(item: typeof props.listings[0]) {
      let iconColor = '#1a73e8' // Bleu par défaut
      let iconEmoji = '🏠'
      
      // Couleur selon le statut
      if (item.status === 'Disponible') {
        iconColor = '#34a853' // Vert
      } else if (item.status === 'Loué') {
        iconColor = '#fbbc04' // Jaune
      } else if (item.status === 'Vendu') {
        iconColor = '#ea4335' // Rouge
      }
      
      // Emoji selon le type
      if (item.type === 'Appartement') {
        iconEmoji = '🏢'
      } else if (item.type === 'Villa') {
        iconEmoji = '🏡'
      } else if (item.type === 'Studio') {
        iconEmoji = '🏘️'
      } else if (item.type === 'Terrain') {
        iconEmoji = '🌳'
      }
      
      return L.divIcon({
        className: 'listing-marker',
        html: `
          <div style="
            position: relative;
            width: 40px;
            height: 40px;
          ">
            <div style="
              position: absolute;
              top: 50%;
              left: 50%;
              transform: translate(-50%, -50%);
              width: 36px;
              height: 36px;
              background: ${iconColor};
              border: 3px solid white;
              border-radius: 50%;
              box-shadow: 0 2px 8px rgba(0,0,0,0.3);
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 18px;
            ">${iconEmoji}</div>
            ${(item as any).rating && (item as any).rating >= 4 ? `
              <div style="
                position: absolute;
                top: -2px;
                right: -2px;
                width: 16px;
                height: 16px;
                background: #fbbc04;
                border: 2px solid white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 10px;
              ">⭐</div>
            ` : ''}
          </div>
        `,
        iconSize: [40, 40],
        iconAnchor: [20, 20]
      })
    }
    
    // Fonction pour afficher les markers
    function renderMarkers() {
      if (!map.value) return
      markersLayer.clearLayers()
    
      if (props.listings.length === 0) {
        // Si pas de listings, mais position utilisateur, centrer sur l'utilisateur
        if (userLocation.value) {
          map.value.setView([userLocation.value.lat, userLocation.value.lng], 13, { animate: true })
        }
        return
      }
    
      const bounds = L.latLngBounds([])
      let validMarkersCount = 0
    
      props.listings.forEach(item => {
        // Ignorer les propriétés sans coordonnées valides
        if (!item.lat || !item.lng || item.lat === 0 || item.lng === 0) {
          return
        }
        
        validMarkersCount++
        const icon = getListingIcon(item)
        const marker = L.marker([item.lat, item.lng], { icon })
          .bindPopup(`
            <div style="min-width: 250px; max-width: 300px;">
              <div style="margin-bottom: 8px;">
                <div style="width: 100%; height: 120px; background: #f3f4f6; border-radius: 8px; margin-bottom: 8px; display: flex; align-items: center; justify-content: center; color: #9ca3af; font-size: 12px;">
                  Image
                </div>
              </div>
              <div style="padding: 0 4px;">
                <strong style="color: #1a73e8; font-size: 14px; display: block; margin-bottom: 4px;">
                  ${item.title}
                </strong>
                <div style="color: #5f6368; font-size: 12px; margin-bottom: 6px;">
                  📍 ${item.city}
                </div>
                <div style="display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 6px; font-size: 11px; color: #5f6368;">
                  <span>${item.type}</span>
                  <span>•</span>
                  <span style="color: ${item.status === 'Disponible' ? '#34a853' : item.status === 'Loué' ? '#fbbc04' : '#ea4335'}; font-weight: 500;">
                    ${item.status}
                  </span>
                </div>
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px;">
                  <strong style="color: #1a73e8; font-size: 16px;">
                    €${item.price.toLocaleString('fr-FR')}
                  </strong>
                  <span style="color: #5f6368; font-size: 12px;">
                    / ${item.surface} m²
                  </span>
                </div>
                ${(item as any).rating ? `
                  <div style="display: flex; align-items: center; gap: 4px; font-size: 11px; color: #5f6368;">
                    <span style="color: #fbbc04;">⭐</span>
                    <span>${(item as any).rating.toFixed(1)}</span>
                    <span>(${(item as any).reviews || 0} avis)</span>
                  </div>
                ` : ''}
                <div style="margin-top: 8px; padding-top: 8px; border-top: 1px solid #e0e0e0;">
                  <button 
                    class="listing-detail-btn"
                    data-listing-id="${item.id}"
                    style="
                      width: 100%;
                      padding: 6px 12px;
                      background: #1a73e8;
                      color: white;
                      border: none;
                      border-radius: 6px;
                      cursor: pointer;
                      font-size: 12px;
                      font-weight: 500;
                      transition: background 0.2s;
                    "
                    onmouseover="this.style.background='#1557b0'"
                    onmouseout="this.style.background='#1a73e8'"
                  >
                    Voir les détails
                  </button>
                </div>
              </div>
            </div>
          `)
          .addTo(markersLayer)
        
        marker.on('click', () => {
          emit('marker-click', item.id)
        })
        
        // Écouter l'événement du bouton dans le popup
        marker.on('popupopen', () => {
          setTimeout(() => {
            const popup = marker.getPopup()
            if (popup && popup.getElement()) {
              const button = popup.getElement()?.querySelector(`button[data-listing-id="${item.id}"]`) as HTMLButtonElement
              if (button) {
                button.onclick = () => {
                  emit('marker-click', item.id)
                }
              }
            }
          }, 100)
        })
        
        bounds.extend([item.lat, item.lng])
      })
      
      // Inclure la position de l'utilisateur dans les bounds si disponible
      if (userLocation.value) {
        bounds.extend([userLocation.value.lat, userLocation.value.lng])
      }
    
      // Ajuster la vue pour afficher tous les markers
      if (validMarkersCount > 0) {
        // Si on a des markers valides, ajuster pour tout afficher
        map.value.fitBounds(bounds, { padding: [50, 50] })
      } else if (userLocation.value) {
        // Si pas de markers mais position utilisateur, zoomer sur la ville
        map.value.setView([userLocation.value.lat, userLocation.value.lng], 13, { animate: true })
      } else {
        // Fallback : vue par défaut (France)
        map.value.setView([46.603354, 1.888334], 6)
      }
    }
    
    // Fonction pour afficher les POI
    function renderPOIs() {
      if (!map.value || !props.interests || props.interests.length === 0) return
      poiLayer.clearLayers()
      
      props.interests.forEach(poi => {
        // Créer une icône personnalisée pour les POI
        const poiIcon = L.divIcon({
          className: 'poi-marker',
          html: `
            <div style="
              background: white;
              border-radius: 50%;
              width: 40px;
              height: 40px;
              display: flex;
              align-items: center;
              justify-content: center;
              box-shadow: 0 2px 8px rgba(0,0,0,0.3);
              border: 2px solid #1a73e8;
            ">
              <span style="font-size: 20px;">${poi.icon}</span>
            </div>
          `,
          iconSize: [40, 40],
          iconAnchor: [20, 20]
        })
        
        L.marker([poi.lat, poi.lng], { icon: poiIcon })
          .bindPopup(`
            <div style="min-width: 180px;">
              <strong style="color: #1a73e8;">${poi.name}</strong><br>
              <span style="color: #5f6368;">${poi.type}</span>
            </div>
          `)
          .addTo(poiLayer)
      })
    }
    </script>
    
    <template>
      <div class="relative">
        <div id="map-view-container" class="w-full h-[500px] rounded-2xl shadow-md overflow-hidden" />
        
        <!-- Compteur d'annonces sur la map -->
        <div
          v-if="props.listings.length > 0"
          class="absolute top-4 left-4 z-[1000] bg-white rounded-lg shadow-md px-3 py-2 border border-gray-200"
        >
          <div class="text-sm font-semibold text-gray-800">
            {{ props.listings.length }} annonce{{ props.listings.length > 1 ? 's' : '' }}
          </div>
        </div>
        
        <!-- Légende des markers -->
        <div class="absolute bottom-4 left-4 z-[1000] bg-white rounded-lg shadow-lg p-3 border border-gray-200">
          <div class="text-xs font-semibold text-gray-700 mb-2">Légende</div>
          <div class="space-y-1.5 text-xs">
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 rounded-full bg-[#34a853] border-2 border-white shadow-sm"></div>
              <span class="text-gray-600">Disponible</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 rounded-full bg-[#fbbc04] border-2 border-white shadow-sm"></div>
              <span class="text-gray-600">Loué</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 rounded-full bg-[#ea4335] border-2 border-white shadow-sm"></div>
              <span class="text-gray-600">Vendu</span>
            </div>
            <div class="flex items-center gap-2 mt-2 pt-2 border-t border-gray-200">
              <div class="w-4 h-4 rounded-full bg-[#4285f4] border-2 border-white shadow-sm"></div>
              <span class="text-gray-600">Votre position</span>
            </div>
          </div>
        </div>
        
        <!-- Bouton pour centrer sur la position de l'utilisateur -->
        <button
          v-if="userLocation || locationError"
          @click="centerOnUserLocation"
          class="absolute top-4 right-4 z-[1000] bg-white hover:bg-gray-50 border border-gray-300 rounded-lg px-4 py-2 shadow-md flex items-center gap-2 text-sm font-medium text-gray-700 transition-colors"
          :title="userLocation ? 'Centrer sur ma position' : 'Obtenir ma position'"
        >
          <span>📍</span>
          <span>{{ userLocation ? 'Ma position' : 'Localiser' }}</span>
        </button>
        
        <!-- Message d'erreur de géolocalisation -->
        <div
          v-if="locationError"
          class="absolute bottom-4 right-4 z-[1000] bg-yellow-50 border border-yellow-200 rounded-lg px-4 py-2 shadow-md text-sm text-yellow-800 max-w-xs"
        >
          <div class="flex items-center gap-2">
            <span>⚠️</span>
            <span>{{ locationError }}</span>
          </div>
        </div>
      </div>
    </template>
    
    <style scoped>
    @keyframes pulse {
      0% {
        transform: translate(-50%, -50%) scale(1);
        opacity: 0.2;
      }
      50% {
        transform: translate(-50%, -50%) scale(1.5);
        opacity: 0.1;
      }
      100% {
        transform: translate(-50%, -50%) scale(1);
        opacity: 0.2;
      }
    }
    </style>
    