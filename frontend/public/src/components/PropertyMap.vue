<template>
  <div id="property-map-container" class="w-full h-full"></div>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const props = defineProps<{
  property: {
    id: number
    title: string
    latitude?: number | null
    longitude?: number | null
    address?: string
    city?: string
  }
  hideActionButton?: boolean
  hideSidebar?: boolean
}>()

// Vérifier que property est défini
if (!props.property) {
  console.error('PropertyMap: property prop is required')
}

let map: L.Map | null = null
let marker: L.Marker | null = null

onMounted(() => {
  initMap()
})

watch(() => [props.property?.latitude, props.property?.longitude], () => {
  if (map && props.property) {
    updateMarker()
  }
})

function initMap() {
  if (!props.property) return
  
  const lat = props.property.latitude ? Number(props.property.latitude) : 48.8566
  const lng = props.property.longitude ? Number(props.property.longitude) : 2.3522

  map = L.map('property-map-container').setView([lat, lng], 15)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap',
  }).addTo(map)

  updateMarker()
}

function updateMarker() {
  if (!map || !props.property) return

  const lat = props.property.latitude ? Number(props.property.latitude) : 48.8566
  const lng = props.property.longitude ? Number(props.property.longitude) : 2.3522

  // Supprimer l'ancien marker
  if (marker) {
    map.removeLayer(marker)
  }

  // Créer une icône personnalisée
  const icon = L.divIcon({
    className: 'property-marker',
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
          background: #1a73e8;
          border: 3px solid white;
          border-radius: 50%;
          box-shadow: 0 2px 8px rgba(0,0,0,0.3);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
        ">🏠</div>
      </div>
    `,
    iconSize: [40, 40],
    iconAnchor: [20, 20]
  })

  // Créer le marker
  marker = L.marker([lat, lng], { icon })
    .bindPopup(`
      <div style="min-width: 200px;">
        <strong style="color: #1a73e8; font-size: 14px; display: block; margin-bottom: 4px;">
          ${props.property.title}
        </strong>
        <div style="color: #5f6368; font-size: 12px;">
          📍 ${props.property.address || props.property.city || 'Non spécifié'}
        </div>
      </div>
    `)
    .addTo(map)

  // Centrer la map sur le marker
  map.setView([lat, lng], 15, { animate: true })
}
</script>

