<template>
  <div class="w-full h-[300px]">
    <ChartContainer v-if="chartData.length > 0" :config="chartConfig" class="h-full">
      <VisXYContainer :data="chartData" :height="300">
        <VisGroupedBar 
          :x="xAccessor" 
          :y="yAccessor" 
          :color="[chartConfig.city.color]"
        />
        <VisAxis 
          type="x" 
          :tick-format="formatXAxis"
          :tick-values="tickValues"
          :tick-line="false"
          :domain-line="false"
          :grid-line="false"
        />
        <VisAxis 
          type="y" 
          :tick-line="false"
          :domain-line="false"
          :grid-line="true"
        />
      </VisXYContainer>
    </ChartContainer>
    <div v-else class="flex items-center justify-center h-full text-muted-foreground">
      Aucune donnée disponible
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { VisXYContainer, VisGroupedBar, VisAxis } from '@unovis/vue'
import ChartContainer from '@/components/ui/chart/ChartContainer.vue'
import type { ChartConfig } from '@/components/ui/chart/interface'
// Type local pour éviter l'erreur d'import
interface DashboardStats {
  propertyStats?: {
    byCity?: Record<string, number>
  }
}

interface Props {
  stats: DashboardStats | null
}

interface ChartData {
  city: string
  count: number
}

const props = defineProps<Props>()

// Configuration du graphique selon shadcn-vue
const chartConfig = {
  city: {
    label: 'Propriétés',
    color: 'hsl(var(--chart-1))'
  }
} satisfies ChartConfig

const chartData = computed<ChartData[]>(() => {
  if (!props.stats?.propertyStats?.byCity) {
    return []
  }
  
  const cityData = props.stats.propertyStats.byCity
  return Object.entries(cityData)
    .map(([city, count]) => ({
      city,
      count: Number(count)
    }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 10) // Top 10 villes
})

// Accesseurs pour le graphique
const xAccessor = (_d: ChartData, i: number) => i
const yAccessor = (d: ChartData) => d.count

// Valeurs pour les ticks de l'axe X
const tickValues = computed(() => chartData.value.map((_, i) => i))

// Formatage des labels de l'axe X
const formatXAxis = (i: number) => {
  if (i >= 0 && i < chartData.value.length) {
    return chartData.value[i].city
  }
  return ''
}
</script>
