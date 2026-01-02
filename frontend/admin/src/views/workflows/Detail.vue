<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <Button variant="ghost" size="sm" @click="$router.push('/workflows')">
          <ArrowLeft class="mr-2 h-4 w-4" />
          {{ t('common.back') }}
        </Button>
        <div>
          <h1 class="text-2.5xl font-bold">{{ workflow?.name || t('workflows.loading') }}</h1>
          <p class="text-muted-foreground mt-1">{{ workflow?.description || '' }}</p>
        </div>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" @click="refreshData" :disabled="loading">
          <RefreshCw class="mr-2 h-4 w-4" :class="{ 'animate-spin': loading }" />
          {{ t('common.refresh') }}
        </Button>
        <Button variant="outline" @click="editWorkflow" :disabled="!workflow">
          <Edit class="mr-2 h-4 w-4" />
          {{ t('common.edit') }}
        </Button>
        <Button @click="openStartDialog" :disabled="!workflow || !workflow.active">
          <Play class="mr-2 h-4 w-4" />
          {{ t('workflows.startWorkflow') }}
        </Button>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-12">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
    </div>

    <div v-else-if="!workflow" class="text-center py-12">
      <p class="text-muted-foreground">{{ t('workflows.notFound') }}</p>
    </div>

    <div v-else class="space-y-6">
      <!-- Informations du Workflow -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Informations principales -->
        <Card class="lg:col-span-2">
          <CardHeader>
            <CardTitle>{{ t('workflows.workflowInformation') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <Label class="text-muted-foreground">{{ t('workflows.name') }}</Label>
                <p class="font-medium">{{ workflow.name }}</p>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('workflows.action') }}</Label>
                <p class="font-medium">{{ getActionLabel(workflow.action) }}</p>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('workflows.status') }}</Label>
                <div class="flex items-center gap-2">
                  <Badge :variant="workflow.active ? 'default' : 'secondary'">
                    {{ workflow.active ? t('workflows.active') : t('workflows.inactive') }}
                  </Badge>
                  <Badge v-if="workflow.isDefault" variant="outline">
                    {{ t('workflows.default') }}
                  </Badge>
                </div>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('workflows.targetType') }}</Label>
                <p class="font-medium">{{ workflow.targetType || t('common.none') }}</p>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('common.createdAt') }}</Label>
                <p class="font-medium">{{ formatDateTime(workflow.createdAt) }}</p>
              </div>
              <div>
                <Label class="text-muted-foreground">{{ t('common.updatedAt') }}</Label>
                <p class="font-medium">{{ workflow.updatedAt ? formatDateTime(workflow.updatedAt) : t('common.never') }}</p>
              </div>
            </div>
            <div v-if="workflow.description">
              <Label class="text-muted-foreground">{{ t('workflows.descriptionLabel') }}</Label>
              <p class="mt-1">{{ workflow.description }}</p>
            </div>
          </CardContent>
        </Card>

        <!-- Statistiques -->
        <Card>
          <CardHeader>
            <CardTitle>{{ t('workflows.statistics') }}</CardTitle>
          </CardHeader>
          <CardContent class="space-y-4">
            <div>
              <Label class="text-muted-foreground">{{ t('workflows.totalTasks') }}</Label>
              <p class="text-2xl font-bold">{{ tasks.length }}</p>
            </div>
            <div>
              <Label class="text-muted-foreground">{{ t('workflows.pendingTasks') }}</Label>
              <p class="text-2xl font-bold text-yellow-600">{{ pendingTasksCount }}</p>
            </div>
            <div>
              <Label class="text-muted-foreground">{{ t('workflows.approvedTasks') }}</Label>
              <p class="text-2xl font-bold text-green-600">{{ approvedTasksCount }}</p>
            </div>
            <div>
              <Label class="text-muted-foreground">{{ t('workflows.rejectedTasks') }}</Label>
              <p class="text-2xl font-bold text-red-600">{{ rejectedTasksCount }}</p>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Timeline des Tâches -->
      <Card>
        <CardHeader>
          <CardTitle>{{ t('workflows.tasksTimeline') }}</CardTitle>
          <CardDescription>{{ t('workflows.tasksTimelineDescription') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="tasks.length === 0" class="text-center py-8 text-muted-foreground">
            {{ t('workflows.noTasks') }}
          </div>
          <div v-else class="space-y-4">
            <div
              v-for="(task, index) in sortedTasks"
              :key="task.id"
              class="relative"
            >
              <!-- Ligne de connexion -->
              <div
                v-if="index < sortedTasks.length - 1"
                class="absolute left-5 top-12 bottom-0 w-0.5 bg-border"
              ></div>

              <!-- Tâche -->
              <div class="flex items-start gap-4">
                <!-- Icône de statut -->
                <div
                  :class="[
                    'flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center',
                    getTaskStatusColorClass(task.status)
                  ]"
                >
                  <component :is="getTaskStatusIcon(task.status)" class="h-5 w-5" />
                </div>

                <!-- Contenu de la tâche -->
                <div class="flex-1 min-w-0 bg-muted/50 rounded-lg p-4">
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-2">
                        <h3 class="font-medium">{{ task.title }}</h3>
                        <Badge :variant="getTaskStatusBadgeVariant(task.status)">
                          {{ getTaskStatusLabel(task.status) }}
                        </Badge>
                        <Badge v-if="task.stepNumber" variant="outline">
                          {{ t('workflows.step') }} {{ task.stepNumber }}
                        </Badge>
                      </div>
                      <p v-if="task.description" class="text-sm text-muted-foreground mb-2">
                        {{ task.description }}
                      </p>
                      <div class="flex flex-wrap items-center gap-4 text-xs text-muted-foreground">
                        <span v-if="task.assignedRole">
                          <strong>{{ t('workflows.assignedRole') }}:</strong> {{ task.assignedRole }}
                        </span>
                        <span v-if="task.dueDate">
                          <strong>{{ t('workflows.dueDate') }}:</strong> {{ formatDateTime(task.dueDate) }}
                        </span>
                        <span v-if="task.completedAt">
                          <strong>{{ t('workflows.completedAt') }}:</strong> {{ formatDateTime(task.completedAt) }}
                        </span>
                      </div>
                      <div v-if="task.comments" class="mt-2 p-2 bg-background rounded text-sm">
                        <strong>{{ t('workflows.comments') }}:</strong> {{ task.comments }}
                      </div>
                    </div>
                    <div class="flex items-center gap-2 ml-4">
                      <Button
                        v-if="canApproveTask(task)"
                        variant="outline"
                        size="sm"
                        @click="approveTask(task)"
                      >
                        <CheckCircle class="mr-2 h-4 w-4" />
                        {{ t('workflows.approve') }}
                      </Button>
                      <Button
                        v-if="canRejectTask(task)"
                        variant="outline"
                        size="sm"
                        @click="rejectTask(task)"
                        class="text-destructive"
                      >
                        <XCircle class="mr-2 h-4 w-4" />
                        {{ t('workflows.reject') }}
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Configuration (Steps et Roles) -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>{{ t('workflows.steps') }}</CardTitle>
            <CardDescription>{{ t('workflows.stepsDescription') }}</CardDescription>
          </CardHeader>
          <CardContent>
            <WorkflowStepsDisplay :steps-json="workflow.steps" />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>{{ t('workflows.requiredRoles') }}</CardTitle>
            <CardDescription>{{ t('workflows.requiredRolesDescription') }}</CardDescription>
          </CardHeader>
          <CardContent>
            <RequiredRolesDisplay :roles-json="workflow.requiredRoles" />
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Dialog pour démarrer le workflow -->
    <Dialog :open="startDialogOpen" @update:open="startDialogOpen = $event">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{ t('workflows.startWorkflow') }}</DialogTitle>
          <DialogDescription>{{ t('workflows.startWorkflowDescription') }}</DialogDescription>
        </DialogHeader>
        <form @submit.prevent="handleStartWorkflow" class="space-y-4">
          <div class="space-y-2">
            <Label for="targetId">{{ t('workflows.targetId') }} *</Label>
            <Input
              id="targetId"
              v-model.number="startTargetId"
              type="number"
              :placeholder="t('workflows.targetIdPlaceholder')"
              required
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" @click="startDialogOpen = false">
              {{ t('common.cancel') }}
            </Button>
            <Button type="submit" :disabled="submitting">
              {{ t('workflows.start') }}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>

    <!-- Dialog pour approuver/rejeter une tâche -->
    <Dialog :open="taskActionDialogOpen" @update:open="taskActionDialogOpen = $event">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{ taskAction === 'approve' ? t('workflows.approveTask') : t('workflows.rejectTask') }}</DialogTitle>
          <DialogDescription>
            {{ taskAction === 'approve' ? t('workflows.approveTaskDescription') : t('workflows.rejectTaskDescription') }}
          </DialogDescription>
        </DialogHeader>
        <form @submit.prevent="handleTaskAction" class="space-y-4">
          <div class="space-y-2">
            <Label for="comments">{{ t('workflows.comments') }}</Label>
            <Textarea
              id="comments"
              v-model="taskComments"
              :placeholder="t('workflows.commentsPlaceholder')"
              rows="4"
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" @click="taskActionDialogOpen = false">
              {{ t('common.cancel') }}
            </Button>
            <Button type="submit" :disabled="submitting" :variant="taskAction === 'reject' ? 'destructive' : 'default'">
              {{ taskAction === 'approve' ? t('workflows.approve') : t('workflows.reject') }}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>

    <!-- Dialog d'édition -->
    <WorkflowDialog
      :open="editDialogOpen"
      @update:open="editDialogOpen = $event"
      :workflow="workflow"
      @saved="handleWorkflowSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@viridial/shared'
import { workflowService } from '@viridial/shared'
import type { Workflow, Task } from '@viridial/shared'
import { TaskStatusEnum } from '@viridial/shared'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useToast } from '@/components/ui/toast'
import { ArrowLeft, RefreshCw, Edit, Play, CheckCircle, XCircle, Clock, AlertCircle } from 'lucide-vue-next'
import WorkflowDialog from '@/components/workflows/WorkflowDialog.vue'
import WorkflowStepsDisplay from '@/components/workflows/WorkflowStepsDisplay.vue'
import RequiredRolesDisplay from '@/components/workflows/RequiredRolesDisplay.vue'

const { t } = useI18n()
const { toast } = useToast()
const route = useRoute()
const authStore = useAuthStore()

// State
const workflow = ref<Workflow | null>(null)
const tasks = ref<Task[]>([])
const loading = ref(false)
const submitting = ref(false)
const startDialogOpen = ref(false)
const startTargetId = ref<number | undefined>(undefined)
const taskActionDialogOpen = ref(false)
const taskAction = ref<'approve' | 'reject'>('approve')
const selectedTask = ref<Task | null>(null)
const taskComments = ref('')
const editDialogOpen = ref(false)

// Computed
const workflowId = computed(() => Number(route.params.id))

const sortedTasks = computed(() => {
  return [...tasks.value].sort((a, b) => (a.stepNumber || 0) - (b.stepNumber || 0))
})

const pendingTasksCount = computed(() => {
  return tasks.value.filter(t => t.status === TaskStatusEnum.PENDING || t.status === TaskStatusEnum.IN_PROGRESS).length
})

const approvedTasksCount = computed(() => {
  return tasks.value.filter(t => t.status === TaskStatusEnum.APPROVED).length
})

const rejectedTasksCount = computed(() => {
  return tasks.value.filter(t => t.status === TaskStatusEnum.REJECTED).length
})

// Methods
const loadWorkflow = async () => {
  loading.value = true
  try {
    const data = await workflowService.getById(workflowId.value)
    workflow.value = data
  } catch (error: any) {
    console.error('Error loading workflow:', error)
    toast({
      title: t('common.error'),
      description: t('workflows.errors.loadWorkflowFailed'),
      variant: 'destructive'
    })
  } finally {
    loading.value = false
  }
}

const loadTasks = async () => {
  try {
    const id = workflowId.value
    if (!id || isNaN(id)) {
      console.warn('Invalid workflow ID, skipping task load')
      return
    }
    const data = await workflowService.getTasks({ workflowId: id })
    tasks.value = data
  } catch (error) {
    console.warn('Could not load tasks:', error)
  }
}

const refreshData = async () => {
  await Promise.all([loadWorkflow(), loadTasks()])
}

const editWorkflow = () => {
  editDialogOpen.value = true
}

const handleWorkflowSaved = () => {
  editDialogOpen.value = false
  loadWorkflow()
}

const openStartDialog = () => {
  startTargetId.value = undefined
  startDialogOpen.value = true
}

const handleStartWorkflow = async () => {
  if (!startTargetId.value) return

  submitting.value = true
  try {
    await workflowService.start(workflowId.value, startTargetId.value)
    toast({
      title: t('common.success'),
      description: t('workflows.started')
    })
    startDialogOpen.value = false
    await loadTasks()
  } catch (error: any) {
    console.error('Error starting workflow:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('workflows.errors.startFailed'),
      variant: 'destructive'
    })
  } finally {
    submitting.value = false
  }
}

const canApproveTask = (task: Task): boolean => {
  const currentUserId = authStore.user?.id
  if (!currentUserId) return false
  return (task.status === TaskStatusEnum.PENDING || task.status === TaskStatusEnum.IN_PROGRESS) &&
         (task.assignedTo === currentUserId || !task.assignedTo)
}

const canRejectTask = (task: Task): boolean => {
  const currentUserId = authStore.user?.id
  if (!currentUserId) return false
  return (task.status === TaskStatusEnum.PENDING || task.status === TaskStatusEnum.IN_PROGRESS) &&
         (task.assignedTo === currentUserId || !task.assignedTo)
}

const approveTask = (task: Task) => {
  selectedTask.value = task
  taskAction.value = 'approve'
  taskComments.value = ''
  taskActionDialogOpen.value = true
}

const rejectTask = (task: Task) => {
  selectedTask.value = task
  taskAction.value = 'reject'
  taskComments.value = ''
  taskActionDialogOpen.value = true
}

const handleTaskAction = async () => {
  if (!selectedTask.value) return

  submitting.value = true
  try {
    const currentUserId = authStore.user?.id
    if (!currentUserId) {
      throw new Error('User not authenticated')
    }

    if (taskAction.value === 'approve') {
      await workflowService.approveTask(selectedTask.value.id, {
        completedBy: currentUserId,
        comments: taskComments.value || undefined
      })
      toast({
        title: t('common.success'),
        description: t('workflows.taskApproved')
      })
    } else {
      await workflowService.rejectTask(selectedTask.value.id, {
        completedBy: currentUserId,
        comments: taskComments.value || undefined
      })
      toast({
        title: t('common.success'),
        description: t('workflows.taskRejected')
      })
    }

    taskActionDialogOpen.value = false
    selectedTask.value = null
    taskComments.value = ''
    await loadTasks()
  } catch (error: any) {
    console.error('Error processing task:', error)
    toast({
      title: t('common.error'),
      description: error.message || t('workflows.errors.taskActionFailed'),
      variant: 'destructive'
    })
  } finally {
    submitting.value = false
  }
}

const getActionLabel = (action: string): string => {
  const actionLabels: Record<string, string> = {
    'PROPERTY_CREATE': t('workflows.actions.propertyCreate'),
    'PROPERTY_UPDATE': t('workflows.actions.propertyUpdate'),
    'PROPERTY_DELETE': t('workflows.actions.propertyDelete'),
    'DOCUMENT_UPLOAD': t('workflows.actions.documentUpload'),
    'USER_CREATE': t('workflows.actions.userCreate')
  }
  return actionLabels[action] || action
}

const getTaskStatusLabel = (status: string): string => {
  const statusLabels: Record<string, string> = {
    'PENDING': t('workflows.taskStatus.pending'),
    'IN_PROGRESS': t('workflows.taskStatus.inProgress'),
    'APPROVED': t('workflows.taskStatus.approved'),
    'REJECTED': t('workflows.taskStatus.rejected'),
    'CANCELLED': t('workflows.taskStatus.cancelled')
  }
  return statusLabels[status] || status
}

const getTaskStatusIcon = (status: string) => {
  const icons: Record<string, any> = {
    'PENDING': Clock,
    'IN_PROGRESS': AlertCircle,
    'APPROVED': CheckCircle,
    'REJECTED': XCircle,
    'CANCELLED': XCircle
  }
  return icons[status] || Clock
}

const getTaskStatusColorClass = (status: string): string => {
  const classes: Record<string, string> = {
    'PENDING': 'bg-yellow-500/10 text-yellow-600',
    'IN_PROGRESS': 'bg-blue-500/10 text-blue-600',
    'APPROVED': 'bg-green-500/10 text-green-600',
    'REJECTED': 'bg-red-500/10 text-red-600',
    'CANCELLED': 'bg-gray-500/10 text-gray-600'
  }
  return classes[status] || 'bg-gray-500/10 text-gray-600'
}

const getTaskStatusBadgeVariant = (status: string): 'default' | 'secondary' | 'destructive' | 'outline' => {
  const variants: Record<string, 'default' | 'secondary' | 'destructive' | 'outline'> = {
    'PENDING': 'secondary',
    'IN_PROGRESS': 'default',
    'APPROVED': 'default',
    'REJECTED': 'destructive',
    'CANCELLED': 'outline'
  }
  return variants[status] || 'secondary'
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

