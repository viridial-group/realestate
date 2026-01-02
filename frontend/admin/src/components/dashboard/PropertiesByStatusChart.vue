<template>
  <div class="w-full h-[300px]">
    <Doughnut :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend
} from 'chart.js'
import type { Property } from '@viridial/shared'

ChartJS.register(ArcElement, Tooltip, Legend)

// Helper pour obtenir les valeurs CSS réelles
const getCSSVariable = (varName: string): string => {
  if (typeof window !== 'undefined') {
    const value = getComputedStyle(document.documentElement).getPropertyValue(varName).trim()
    return value ? `hsl(${value})` : 'hsl(222.2, 84%, 4.9%)'
  }
  return 'hsl(222.2, 84%, 4.9%)'
}

interface Props {
  properties: Property[]
}

const props = defineProps<Props>()

// Palette de couleurs vibrantes basée sur les images (valeurs HSL complètes)
const COLORS = [
  'hsl(270, 91%, 65%)', // Purple
  'hsl(330, 81%, 60%)', // Pink
  'hsl(25, 95%, 53%)', // Orange
  'hsl(195, 100%, 50%)', // Cyan/Blue
  'hsl(142, 76%, 36%)', // Green
  'hsl(0, 84%, 60%)' // Red/Coral
]

const chartData = computed(() => {
  const statusCounts: Record<string, number> = {}
  
  props.properties.forEach(property => {
    const status = property.status || 'DRAFT'
    statusCounts[status] = (statusCounts[status] || 0) + 1
  })

  const statusLabels: Record<string, string> = {
    'AVAILABLE': 'Disponible',
    'SOLD': 'Vendu',
    'RENTED': 'Loué',
    'PENDING': 'En attente',
    'DRAFT': 'Brouillon',
    'PUBLISHED': 'Publié'
  }

  const entries = Object.entries(statusCounts).map(([status, count]) => ({
    label: statusLabels[status] || status,
    value: count
  }))

  return {
    labels: entries.map(e => e.label),
    datasets: [
      {
        data: entries.map(e => e.value),
        backgroundColor: COLORS.slice(0, entries.length),
        borderColor: getCSSVariable('--background'),
        borderWidth: 2
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        color: getCSSVariable('--foreground'),
        padding: 15,
        usePointStyle: true
      }
    },
    tooltip: {
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      titleColor: 'rgb(15, 23, 42)',
      bodyColor: 'rgb(15, 23, 42)',
      borderColor: 'rgba(226, 232, 240, 1)',
      borderWidth: 1,
      padding: 12,
      displayColors: true,
      boxPadding: 6
    }
  }
}
</script>
