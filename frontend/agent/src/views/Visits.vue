<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h1 class="text-3xl font-bold">{{ t('visits.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('visits.description') }}</p>
      </div>
      <Button @click="openCreateDialog" size="lg">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('visits.createVisit') }}
      </Button>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="space-y-2">
            <Label>{{ t('visits.status') }}</Label>
            <Select v-model="selectedStatus" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="null">{{ t('common.all') }}</SelectItem>
                <SelectItem value="UPCOMING">{{ t('visits.upcoming') }}</SelectItem>
                <SelectItem value="COMPLETED">{{ t('visits.completed') }}</SelectItem>
                <SelectItem value="CANCELLED">{{ t('visits.cancelled') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>{{ t('common.date') }}</Label>
            <Input type="date" v-model="selectedDate" @input="handleFilter" />
          </div>
          <div class="space-y-2">
            <Label>{{ t('common.actions') }}</Label>
            <Button variant="outline" class="w-full" @click="resetFilters">
              <X class="mr-2 h-4 w-4" />
              {{ t('common.reset') }}
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des visites -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin text-muted-foreground" />
    </div>
    <div v-else-if="visits.length === 0" class="text-center py-12">
      <Calendar class="h-12 w-12 text-muted-foreground mx-auto mb-4" />
      <p class="text-muted-foreground">{{ t('visits.noVisits') }}</p>
      <Button class="mt-4" @click="openCreateDialog">
        <Plus class="mr-2 h-4 w-4" />
        {{ t('visits.createVisit') }}
      </Button>
    </div>
    <div v-else class="space-y-4">
      <Card
        v-for="visit in visits"
        :key="visit.id"
        class="cursor-pointer hover:shadow-lg transition-shadow"
        @click="viewVisit(visit.id)"
      >
        <CardHeader>
          <div class="flex items-center justify-between">
            <CardTitle>{{ visit.property?.title || t('visits.property') }}</CardTitle>
            <Badge :variant="getStatusVariant(visit.status)">
              {{ getStatusLabel(visit.status) }}
            </Badge>
          </div>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <Calendar class="h-4 w-4 text-muted-foreground" />
              <span class="text-sm">{{ formatDateTime(visit.date) }}</span>
            </div>
            <div class="flex items-center gap-2">
              <Users class="h-4 w-4 text-muted-foreground" />
              <span class="text-sm">{{ visit.client?.name || t('visits.client') }}</span>
            </div>
            <div v-if="visit.notes" class="mt-4">
              <p class="text-sm text-muted-foreground">{{ visit.notes }}</p>
            </div>
          </div>
        </CardContent>
        <CardFooter class="flex justify-between gap-2">
          <Button variant="outline" size="sm" class="flex-1" @click.stop="editVisit(visit.id)">
            <Edit class="mr-2 h-4 w-4" />
            {{ t('common.edit') }}
          </Button>
          <Button variant="outline" size="sm" class="flex-1 text-destructive" @click.stop="deleteVisit(visit.id)">
            <Trash2 class="mr-2 h-4 w-4" />
            {{ t('common.delete') }}
          </Button>
        </CardFooter>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from '@/components/ui/toast'
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import {
  Plus,
  X,
  Calendar,
  Edit,
  Trash2,
  Users,
  Loader2
} from 'lucide-vue-next'

const { t } = useI18n()
const { toast } = useToast()

const loading = ref(false)
const selectedStatus = ref<string | null>(null)
const selectedDate = ref('')
const visits = ref<any[]>([])

// TODO: Implement visit service
const loadVisits = async () => {
  loading.value = true
  try {
    // Placeholder - implement with actual visit service
    visits.value = []
  } catch (error) {
    console.error('Error loading visits:', error)
    toast({
      title: t('common.error'),
      description: 'Erreur lors du chargement des visites',
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  loadVisits()
}

const resetFilters = () => {
  selectedStatus.value = null
  selectedDate.value = ''
  loadVisits()
}

const getStatusVariant = (status: string): 'default' | 'secondary' | 'destructive' | 'outline' => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    'UPCOMING': 'default',
    'COMPLETED': 'secondary',
    'CANCELLED': 'destructive'
  }
  return variants[status] || 'outline'
}

const getStatusLabel = (status: string): string => {
  return t(`visits.${status.toLowerCase()}`) || status
}

const formatDateTime = (date: string | Date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const openCreateDialog = () => {
  // TODO: Implement create visit dialog
  toast({
    title: t('common.info'),
    description: 'Fonctionnalité à venir'
  })
}

const viewVisit = (id: number) => {
  // TODO: Navigate to visit detail
}

const editVisit = (id: number) => {
  // TODO: Open edit dialog
}

const deleteVisit = async (id: number) => {
  if (!confirm(t('visits.deleteVisit'))) return
  // TODO: Implement delete
}

onMounted(() => {
  loadVisits()
})
</script>

