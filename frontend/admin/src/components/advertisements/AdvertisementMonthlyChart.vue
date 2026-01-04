<template>
  <div class="w-full h-[400px]">
    <Line v-if="chartData" :data="chartData" :options="chartOptions" />
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
import type { MonthlyStats } from '@viridial/shared'

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

interface Props {
  data: MonthlyStats[]
}

const props = defineProps<Props>()

const monthLabels: Record<number, string> = {
  1: 'Jan', 2: 'Fév', 3: 'Mar', 4: 'Avr',
  5: 'Mai', 6: 'Jun', 7: 'Jul', 8: 'Aoû',
  9: 'Sep', 10: 'Oct', 11: 'Nov', 12: 'Déc'
}

const chartData = computed(() => {
  if (!props.data || props.data.length === 0) {
    return null
  }

  const sortedData = [...props.data].sort((a, b) => {
    if (a.year !== b.year) return a.year - b.year
    return a.month - b.month
  })

  return {
    labels: sortedData.map(d => `${monthLabels[d.month] || d.month} ${d.year}`),
    datasets: [
      {
        label: 'Impressions',
        data: sortedData.map(d => d.impressions),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: 'rgb(59, 130, 246)',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 5,
        pointHoverRadius: 7
      },
      {
        label: 'Clics',
        data: sortedData.map(d => d.clicks),
        borderColor: 'rgb(168, 85, 247)',
        backgroundColor: 'rgba(168, 85, 247, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: 'rgb(168, 85, 247)',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 5,
        pointHoverRadius: 7
      },
      {
        label: 'Conversions',
        data: sortedData.map(d => d.conversions),
        borderColor: 'rgb(34, 197, 94)',
        backgroundColor: 'rgba(34, 197, 94, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: 'rgb(34, 197, 94)',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 5,
        pointHoverRadius: 7
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top' as const,
      labels: {
        color: 'rgb(107, 114, 128)',
        usePointStyle: true,
        padding: 15
      }
    },
    tooltip: {
      mode: 'index' as const,
      intersect: false,
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      titleColor: 'rgb(255, 255, 255)',
      bodyColor: 'rgb(255, 255, 255)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      borderWidth: 1,
      padding: 12
    }
  },
  scales: {
    x: {
      grid: {
        color: 'rgba(0, 0, 0, 0.05)'
      },
      ticks: {
        color: 'rgb(107, 114, 128)'
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: 'rgba(0, 0, 0, 0.05)'
      },
      ticks: {
        color: 'rgb(107, 114, 128)'
      }
    }
  }
}
</script>

