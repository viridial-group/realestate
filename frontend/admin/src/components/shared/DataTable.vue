<template>
  <div class="space-y-4">
    <!-- Header avec Actions -->
    <div v-if="showHeader" class="flex items-center justify-between flex-wrap gap-4">
      <div>
        <h2 v-if="title" class="text-2xl font-bold">{{ title }}</h2>
        <p v-if="description" class="text-muted-foreground mt-1">{{ description }}</p>
      </div>
      <div class="flex gap-2">
        <Button
          v-if="showExport"
          variant="outline"
          @click="$emit('export')"
          :disabled="loading"
        >
          <Download class="mr-2 h-4 w-4" />
          Exporter
        </Button>
        <Button
          v-if="showCreate"
          @click="$emit('create')"
          size="lg"
        >
          <Plus class="mr-2 h-4 w-4" />
          {{ createLabel }}
        </Button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div v-if="stats && stats.length > 0" class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <Card
        v-for="stat in stats"
        :key="stat.key"
        class="cursor-pointer hover:shadow-md transition-shadow"
        @click="stat.onClick && stat.onClick()"
      >
        <CardHeader class="pb-2">
          <CardDescription>{{ stat.label }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div :class="['text-2xl font-bold', stat.colorClass || '']">
            {{ stat.value }}
          </div>
          <p v-if="stat.subtitle" class="text-xs text-muted-foreground mt-1">
            {{ stat.subtitle }}
          </p>
        </CardContent>
      </Card>
    </div>

    <!-- Filtres -->
    <Card v-if="filters && filters.length > 0">
      <CardContent class="p-4">
        <div :class="['grid gap-4', filterGridClass]">
          <div v-for="filter in filters" :key="filter.key" class="space-y-2">
            <Label v-if="filter.label">{{ filter.label }}</Label>
            <Input
              v-if="filter.type === 'text' || filter.type === 'search'"
              :model-value="filter.value"
              :placeholder="filter.placeholder"
              @update:model-value="(val) => $emit('filter-change', filter.key, val)"
            />
            <Select
              v-else-if="filter.type === 'select'"
              :model-value="filter.value"
              @update:model-value="(val) => $emit('filter-change', filter.key, val)"
            >
              <SelectTrigger>
                <SelectValue :placeholder="filter.placeholder" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="option in filter.options" :key="option.value" :value="option.value">
                  {{ option.label }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div v-if="showResetFilters" class="flex items-end">
            <Button variant="outline" @click="$emit('reset-filters')" class="w-full">
              <X class="mr-2 h-4 w-4" />
              Réinitialiser
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Table -->
    <Card>
      <CardContent class="p-0">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-muted/50">
              <tr>
                <th v-if="selectable" class="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase">
                  <Checkbox
                    :checked="allSelected"
                    @update:checked="(val) => $emit('select-all', val)"
                  />
                </th>
                <th
                  v-for="column in columns"
                  :key="column.key"
                  :class="[
                    'px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider',
                    column.align === 'right' ? 'text-right' : ''
                  ]"
                >
                  <div class="flex items-center gap-2">
                    {{ column.label }}
                    <Button
                      v-if="column.sortable"
                      variant="ghost"
                      size="sm"
                      class="h-4 w-4 p-0"
                      @click="$emit('sort', column.key)"
                    >
                      <ArrowUpDown class="h-3 w-3" />
                    </Button>
                  </div>
                </th>
                <th
                  v-if="actions && actions.length > 0"
                  class="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase tracking-wider"
                >
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-border">
              <tr
                v-for="(row, index) in data"
                :key="getRowKey(row, index)"
                :class="[
                  'hover:bg-muted/50 transition-colors',
                  rowClickable ? 'cursor-pointer' : ''
                ]"
                @click="rowClickable && $emit('row-click', row)"
              >
                <td v-if="selectable" class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <Checkbox
                    :checked="isRowSelected(row)"
                    @update:checked="(val) => $emit('row-select', row, val)"
                  />
                </td>
                <td
                  v-for="column in columns"
                  :key="column.key"
                  :class="[
                    'px-6 py-4',
                    column.align === 'right' ? 'text-right' : '',
                    column.nowrap ? 'whitespace-nowrap' : ''
                  ]"
                >
                  <slot :name="`cell-${column.key}`" :row="row" :value="getRowValue(row, column.key)">
                    {{ formatCellValue(row, column) }}
                  </slot>
                </td>
                <td
                  v-if="actions && actions.length > 0"
                  class="px-6 py-4 whitespace-nowrap text-right"
                  @click.stop
                >
                  <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                      <Button variant="ghost" size="sm">
                        <MoreVertical class="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem
                        v-for="action in actions"
                        :key="action.key"
                        :class="action.destructive ? 'text-destructive' : ''"
                        @click="$emit('action', action.key, row)"
                      >
                        <component :is="action.icon" class="mr-2 h-4 w-4" />
                        {{ action.label }}
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Actions en masse -->
        <div
          v-if="selectable && selectedRows.length > 0"
          class="border-t bg-muted/30 p-4 flex items-center justify-between"
        >
          <div class="text-sm text-muted-foreground">
            {{ selectedRows.length }} élément(s) sélectionné(s)
          </div>
          <div class="flex gap-2">
            <Button
              v-for="bulkAction in bulkActions"
              :key="bulkAction.key"
              variant="outline"
              size="sm"
              :class="bulkAction.destructive ? 'text-destructive' : ''"
              @click="$emit('bulk-action', bulkAction.key)"
            >
              <component :is="bulkAction.icon" class="mr-2 h-4 w-4" />
              {{ bulkAction.label }}
            </Button>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="pagination" class="border-t p-4 flex items-center justify-between">
          <div class="text-sm text-muted-foreground">
            Affichage de {{ pagination.startIndex + 1 }} à {{ pagination.endIndex }} sur {{ pagination.total }}
          </div>
          <div class="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              :disabled="pagination.currentPage === 1"
              @click="$emit('page-change', pagination.currentPage - 1)"
            >
              Précédent
            </Button>
            <Button
              variant="outline"
              size="sm"
              :disabled="pagination.currentPage === pagination.totalPages"
              @click="$emit('page-change', pagination.currentPage + 1)"
            >
              Suivant
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { Plus, Download, X, MoreVertical, ArrowUpDown } from 'lucide-vue-next'

interface Column {
  key: string
  label: string
  align?: 'left' | 'right' | 'center'
  sortable?: boolean
  nowrap?: boolean
  formatter?: (value: any, row: any) => string
}

interface Filter {
  key: string
  label?: string
  type: 'text' | 'search' | 'select'
  placeholder?: string
  value: any
  options?: { value: string; label: string }[]
}

interface Stat {
  key: string
  label: string
  value: string | number
  subtitle?: string
  colorClass?: string
  onClick?: () => void
}

interface Action {
  key: string
  label: string
  icon: any
  destructive?: boolean
}

interface Pagination {
  currentPage: number
  totalPages: number
  total: number
  startIndex: number
  endIndex: number
}

interface Props {
  title?: string
  description?: string
  columns: Column[]
  data: any[]
  loading?: boolean
  selectable?: boolean
  selectedRows?: any[]
  rowClickable?: boolean
  actions?: Action[]
  bulkActions?: Action[]
  filters?: Filter[]
  stats?: Stat[]
  pagination?: Pagination
  showHeader?: boolean
  showExport?: boolean
  showCreate?: boolean
  createLabel?: string
  showResetFilters?: boolean
  filterGridClass?: string
  rowKey?: string | ((row: any) => string | number)
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  selectable: false,
  selectedRows: () => [],
  rowClickable: false,
  showHeader: true,
  showExport: true,
  showCreate: true,
  createLabel: 'Créer',
  showResetFilters: true,
  filterGridClass: 'grid-cols-1 md:grid-cols-5',
  rowKey: 'id'
})

const emit = defineEmits<{
  'create': []
  'export': []
  'filter-change': [key: string, value: any]
  'reset-filters': []
  'select-all': [value: boolean]
  'row-select': [row: any, value: boolean]
  'row-click': [row: any]
  'action': [key: string, row: any]
  'bulk-action': [key: string]
  'sort': [key: string]
  'page-change': [page: number]
}>()

const allSelected = computed(() => {
  if (!props.selectable || props.data.length === 0) return false
  return props.selectedRows.length === props.data.length
})

const getRowKey = (row: any, index: number): string | number => {
  if (typeof props.rowKey === 'function') {
    return props.rowKey(row)
  }
  return row[props.rowKey] || index
}

const isRowSelected = (row: any): boolean => {
  if (!props.selectable) return false
  const key = getRowKey(row, 0)
  return props.selectedRows.some((r) => getRowKey(r, 0) === key)
}

const getRowValue = (row: any, key: string): any => {
  const keys = key.split('.')
  let value = row
  for (const k of keys) {
    value = value?.[k]
  }
  return value
}

const formatCellValue = (row: any, column: Column): string => {
  const value = getRowValue(row, column.key)
  if (column.formatter) {
    return column.formatter(value, row)
  }
  if (value === null || value === undefined) return '-'
  if (typeof value === 'object') return JSON.stringify(value)
  return String(value)
}
</script>

