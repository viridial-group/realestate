<template>
  <div class="w-full h-[300px]">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import type { Property } from '@viridial/shared'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
)

// Helper pour obtenir les valeurs CSS réelles
const getCSSVariable = (varName: string): string => {
  if (typeof window !== 'undefined') {
    const value = getComputedStyle(document.documentElement).getPropertyValue(varName).trim()
    return value ? `hsl(${value})` : 'hsl(270, 91%, 65%)'
  }
  return 'hsl(270, 91%, 65%)'
}

interface Props {
  properties: Property[]
}

const props = defineProps<Props>()

const chartData = computed(() => {
  const typeCounts: Record<string, number> = {}
  
  props.properties.forEach(property => {
    const type = (property as any).type || (property as any).propertyType || 'OTHER'
    typeCounts[type] = (typeCounts[type] || 0) + 1
  })

  const typeLabels: Record<string, string> = {
    'APARTMENT': 'Appartement',
    'HOUSE': 'Maison',
    'VILLA': 'Villa',
    'LAND': 'Terrain',
    'COMMERCIAL': 'Commercial',
    'OTHER': 'Autre'
  }

  // Palette de couleurs vibrantes pour chaque type (valeurs HSL complètes)
  const typeColors: Record<string, string> = {
    'APARTMENT': 'hsl(270, 91%, 65%)', // Purple
    'HOUSE': 'hsl(330, 81%, 60%)', // Pink
    'VILLA': 'hsl(25, 95%, 53%)', // Orange
    'LAND': 'hsl(195, 100%, 50%)', // Cyan/Blue
    'COMMERCIAL': 'hsl(142, 76%, 36%)', // Green
    'OTHER': 'hsl(0, 84%, 60%)' // Red/Coral
  }

  const entries = Object.entries(typeCounts)
    .map(([type, count]) => ({
      type,
      label: typeLabels[type] || type,
      value: count,
      color: typeColors[type] || 'hsl(var(--chart-1))'
    }))
    .sort((a, b) => b.value - a.value)

  return {
    labels: entries.map(e => e.label),
    datasets: [
      {
        label: 'Nombre de propriétés',
        data: entries.map(e => e.value),
        backgroundColor: entries.map(e => e.color),
        borderColor: entries.map(e => e.color),
        borderWidth: 1
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
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
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        color: getCSSVariable('--muted-foreground')
      },
      grid: {
        color: getCSSVariable('--border')
      }
    },
    x: {
      ticks: {
        color: getCSSVariable('--muted-foreground')
      },
      grid: {
        color: getCSSVariable('--border')
      }
    }
  }
}
</script>
