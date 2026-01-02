<template>
  <div class="w-full h-[300px]">
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'
import type { Property } from '@viridial/shared'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

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

const chartData = computed(() => {
  const monthCounts: Record<string, number> = {}
  
  props.properties.forEach(property => {
    if (property.createdAt) {
      const date = new Date(property.createdAt)
      const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
      monthCounts[monthKey] = (monthCounts[monthKey] || 0) + 1
    }
  })

  // Convertir en format pour le graphique
  const months = Object.keys(monthCounts).sort()
  const monthLabels: Record<string, string> = {
    '01': 'Jan', '02': 'Fév', '03': 'Mar', '04': 'Avr',
    '05': 'Mai', '06': 'Jun', '07': 'Jul', '08': 'Aoû',
    '09': 'Sep', '10': 'Oct', '11': 'Nov', '12': 'Déc'
  }

  const entries = months.map(monthKey => {
    const [year, month] = monthKey.split('-')
    const monthLabel = monthLabels[month as keyof typeof monthLabels] || month
    return {
      label: `${monthLabel} ${year}`,
      count: monthCounts[monthKey]
    }
  }).slice(-12) // Derniers 12 mois

  return {
    labels: entries.map(e => e.label),
    datasets: [
      {
        label: 'Propriétés créées',
        data: entries.map(e => e.count),
        borderColor: 'hsl(270, 91%, 65%)', // Purple
        backgroundColor: 'hsla(270, 91%, 65%, 0.1)',
        borderWidth: 2,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: 'hsl(270, 91%, 65%)',
        pointBorderColor: getCSSVariable('--background'),
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top' as const,
      labels: {
        color: getCSSVariable('--foreground'),
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
