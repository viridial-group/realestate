<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2.5xl font-bold">{{ t('workflows.title') }}</h1>
        <p class="text-muted-foreground mt-1">{{ t('workflows.descriptionLabel') }}</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          {{ t('common.refresh') }}
        </Button>
        <Button @click="openCreateDialog" size="lg">
          <Plus class="mr-2 h-4 w-4" />
          {{ t('workflows.createWorkflow') }}
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #33d484;" @click="filterByActive(true)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('workflows.activeWorkflows') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(51, 212, 132, 0.1);">
            <CheckCircle class="h-5 w-5" style="color: #33d484;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #33d484;">{{ activeCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('workflows.workflows') }}</p>
        </CardContent>
      </Card>
      
      <Card class="cursor-pointer hover:shadow-md transition-shadow relative overflow-hidden border-l-4" style="border-left-color: #fdb022;" @click="filterByActive(false)">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('workflows.inactiveWorkflows') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(253, 176, 34, 0.1);">
            <XCircle class="h-5 w-5" style="color: #fdb022;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #fdb022;">{{ inactiveCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('workflows.workflows') }}</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4" style="border-left-color: #04c9ff;">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('workflows.totalWorkflows') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg flex items-center justify-center" style="background-color: rgba(4, 201, 255, 0.1);">
            <WorkflowIcon class="h-5 w-5" style="color: #04c9ff;" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold" style="color: #04c9ff;">{{ workflows.length }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('workflows.workflows') }}</p>
        </CardContent>
      </Card>
      
      <Card class="relative overflow-hidden border-l-4 border-l-[hsl(var(--chart-1))]">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardDescription>{{ t('workflows.pendingTasks') }}</CardDescription>
          <div class="h-10 w-10 rounded-lg bg-[hsl(var(--chart-1))]/10 flex items-center justify-center">
            <Clock class="h-5 w-5 text-[hsl(var(--chart-1))]" />
          </div>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ pendingTasksCount }}</div>
          <p class="text-xs text-muted-foreground mt-1">{{ t('workflows.tasks') }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card>
      <CardContent class="p-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="space-y-2">
            <Label>{{ t('common.search') }}</Label>
            <Input v-model="searchQuery" :placeholder="t('workflows.searchPlaceholder')" @input="handleSearch" />
          </div>
          <div class="space-y-2">
            <Label>{{ t('workflows.action') }}</Label>
            <Select v-model="selectedAction" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ t('common.all') }}</SelectItem>
                <SelectItem value="PROPERTY_CREATE">{{ t('workflows.actions.propertyCreate') }}</SelectItem>
                <SelectItem value="PROPERTY_UPDATE">{{ t('workflows.actions.propertyUpdate') }}</SelectItem>
                <SelectItem value="PROPERTY_DELETE">{{ t('workflows.actions.propertyDelete') }}</SelectItem>
                <SelectItem value="DOCUMENT_UPLOAD">{{ t('workflows.actions.documentUpload') }}</SelectItem>
                <SelectItem value="USER_CREATE">{{ t('workflows.actions.userCreate') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="space-y-2">
            <Label>{{ t('workflows.status') }}</Label>
            <Select v-model="selectedActive" @update:model-value="handleFilter">
              <SelectTrigger>
                <SelectValue :placeholder="t('common.all')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{{ t('common.all') }}</SelectItem>
                <SelectItem value="true">{{ t('workflows.active') }}</SelectItem>
                <SelectItem value="false">{{ t('workflows.inactive') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div class="flex items-end">
            <Button variant="outline" @click="resetFilters" class="w-full">
              <X class="mr-2 h-4 w-4" />
              {{ t('common.reset') }}
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Liste des Workflows -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-8 text-center">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          <p class="mt-2 text-sm text-muted-foreground">{{ t('common.loading') }}</p>
        </div>
        <div v-else-if="filteredWorkflows.length === 0" class="p-8 text-center">
          <WorkflowIcon class="mx-auto h-12 w-12 text-muted-foreground mb-4" />
          <p class="text-sm text-muted-foreground">{{ t('workflows.noWorkflowsFound') }}</p>
        </div>
        <div v-else class="divide-y divide-border">
          <div
            v-for="workflow in filteredWorkflows"
            :key="workflow.id"
            class="p-4 hover:bg-muted/50 transition-colors"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <h3 class="text-sm font-medium">{{ workflow.name }}</h3>
                  <Badge v-if="workflow.isDefault" variant="secondary">{{ t('workflows.default') }}</Badge>
                  <Badge :variant="workflow.active ? 'default' : 'secondary'">
                    {{ workflow.active ? t('workflows.active') : t('workflows.inactive') }}
                  </Badge>
                </div>
                <p v-if="workflow.description" class="text-sm text-muted-foreground mt-1">{{ workflow.description }}</p>
                <div class="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
                  <span><strong>{{ t('workflows.action') }}:</strong> {{ workflow.action }}</span>
                  <span v-if="workflow.targetType"><strong>{{ t('workflows.targetType') }}:</strong> {{ workflow.targetType }}</span>
                  <span>{{ t('common.createdAt') }}: {{ formatDateTime(workflow.createdAt) }}</span>
                </div>
              </div>
              <div class="flex items-center gap-2 ml-4">
                <Button
                  variant="ghost"
                  size="sm"
                  @click="viewWorkflow(workflow.id)"
                  :title="t('common.view')"
                >
                  <Eye class="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  @click="editWorkflow(workflow)"
                  :title="t('common.edit')"
                >
                  <Edit class="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  @click="deleteWorkflow(workflow.id)"
                  class="text-destructive"
                  :title="t('common.delete')"
                >
                  <Trash2 class="h-4 w-4" />
                </Button>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Dialog de création/édition -->
    <WorkflowDialog
      v-model:open="dialogOpen"
      :workflow="selectedWorkflow"
      @saved="handleWorkflowSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { workflowService, type Workflow, type Task, useAuthStore } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { useToast } from '@/components/ui/toast'
import { Workflow as WorkflowIcon, Plus, RefreshCw, CheckCircle, XCircle, Clock, Eye, Edit, Trash2, X } from 'lucide-vue-next'
import WorkflowDialog from '@/components/workflows/WorkflowDialog.vue'

const { t } = useI18n()
const { toast } = useToast()
const router = useRouter()
const authStore = useAuthStore()

// State
const workflows = ref<Workflow[]>([])
const tasks = ref<Task[]>([])
const loading = ref(false)
const searchQuery = ref('')
const selectedAction = ref('all')
const selectedActive = ref('all')
const dialogOpen = ref(false)
const selectedWorkflow = ref<Workflow | null>(null)

// Computed
const activeCount = computed(() => workflows.value.filter(w => w.active).length)
const inactiveCount = computed(() => workflows.value.filter(w => !w.active).length)
const pendingTasksCount = computed(() => tasks.value.filter(t => t.status === 'PENDING' || t.status === 'IN_PROGRESS').length)

const filteredWorkflows = computed(() => {
  let filtered = [...workflows.value]

  // Filtre par recherche
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(w =>
      w.name.toLowerCase().includes(query) ||
      w.description?.toLowerCase().includes(query) ||
      w.action.toLowerCase().includes(query)
    )
  }

  // Filtre par action
  if (selectedAction.value !== 'all') {
    filtered = filtered.filter(w => w.action === selectedAction.value)
  }

  // Filtre par statut actif
  if (selectedActive.value !== 'all') {
    const isActive = selectedActive.value === 'true'
    filtered = filtered.filter(w => w.active === isActive)
  }

  return filtered
})

// Methods
const loadWorkflows = async () => {
  loading.value = true
  try {
    const currentOrgId = authStore.user?.organizationId
    if (currentOrgId) {
      const data = await workflowService.getAll({ organizationId: currentOrgId })
      workflows.value = data
    }
  } catch (error: any) {
    console.error('Error loading workflows:', error)
    toast({
      title: t('common.error'),
      description: t('workflows.errors.loadFailed'),
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadTasks = async () => {
  try {
    const data = await workflowService.getTasks()
    tasks.value = data
  } catch (error) {
    console.warn('Could not load tasks:', error)
  }
}

const refreshData = async () => {
  await Promise.all([loadWorkflows(), loadTasks()])
}

const handleSearch = () => {
  // Le computed filteredWorkflows se met à jour automatiquement
}

const handleFilter = () => {
  // Le computed filteredWorkflows se met à jour automatiquement
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedAction.value = 'all'
  selectedActive.value = 'all'
}

const filterByActive = (active: boolean) => {
  selectedActive.value = active ? 'true' : 'false'
}

const openCreateDialog = () => {
  selectedWorkflow.value = null
  dialogOpen.value = true
}

const editWorkflow = (workflow: Workflow) => {
  selectedWorkflow.value = workflow
  dialogOpen.value = true
}

const viewWorkflow = (id: number) => {
  router.push(`/workflows/${id}`)
}

const deleteWorkflow = async (id: number) => {
  if (!confirm(t('workflows.confirmDelete'))) {
    return
  }

  try {
    await workflowService.delete(id)
    toast({
      title: t('common.success'),
      description: t('workflows.deleted')
    })
    await loadWorkflows()
  } catch (error: any) {
    console.error('Error deleting workflow:', error)
    toast({
      title: t('common.error'),
      description: t('workflows.errors.deleteFailed'),
      variant: 'destructive'
    })
  }
}

const handleWorkflowSaved = () => {
  dialogOpen.value = false
  selectedWorkflow.value = null
  loadWorkflows()
}

const formatDateTime = (date: string) => {
  return new Date(date).toLocaleString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(async () => {
  await refreshData()
})
</script>
